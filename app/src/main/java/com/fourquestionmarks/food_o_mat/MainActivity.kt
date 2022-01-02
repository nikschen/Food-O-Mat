package com.fourquestionmarks.food_o_mat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fourquestionmarks.food_o_mat.data.KeyValueStore
import com.fourquestionmarks.food_o_mat.databinding.ActivityMainBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.fourquestionmarks.food_o_mat.ui.MealViewModel
import com.fourquestionmarks.food_o_mat.ui.MealViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val  PICK_CSV_FOR_IMPORT = 1
const val  CREATE_CSV_FOR_EXPORT = 2

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: MealViewModel
    private lateinit var settings: KeyValueStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //TODO: Entfernen, wenn Night Mode fertig
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = (application as FoodOMatApplication).settings
        viewModel = MealViewModelFactory((application as FoodOMatApplication).database.mealDao()).create(MealViewModel::class.java)

        val navView: BottomNavigationView = binding.navView

        if (settings.getBoolValue("isFirstRun") && false) {
            val inputStream: InputStream = resources.openRawResource(R.raw.mahlzeiten)
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val allLines = reader.readLines()
            (application as FoodOMatApplication).applicationScope.launch {
                importMeals(allLines)}

            settings.writeBoolValue("isFirstRun", false)
        }


        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_random,
                R.id.navigation_dashboard,
                R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private suspend fun importMeals(allLines: List<String>) {

        allLines.forEach {

                //get a string array of all items in this list
                val mealData = it.split(";").toMutableList()
                // generate new meal entry
                val meal = Meal(
                    name=mealData[0],
                    ID=mealData[1].toInt(),
                    category = mealData[2],
                    calories = mealData[3].replace(",", ".").toFloat(),
                    carbohydrates = mealData[4].replace(",", ".").toFloat(),
                    proteins = mealData[5].replace(",", ".").toFloat(),
                    fats = mealData[6].replace(",", ".").toFloat(),
                    isVeggie = mealData[7].toBoolean(),
                    isVegan = mealData[8].toBoolean()
                )

                //insert new meal entry
            viewModel.insertMeal(meal)
        }
        withContext(Dispatchers.Main){
            Toast.makeText(applicationContext, "Mahlzeiten erfolgreich importiert", Toast.LENGTH_SHORT).show()
        }
    }



    private fun openFileForMealImport(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            type= "*/*"
            putExtra("CONTENT_TYPE","text/csv")
        }


        startActivityForResult(intent, PICK_CSV_FOR_IMPORT)
    }

    private fun createFileForMealExport(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            type= "*/*"
            putExtra(Intent.EXTRA_TITLE,"fom_export_${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}.csv")}


        startActivityForResult(intent, CREATE_CSV_FOR_EXPORT)
    }

    private fun shareFileAfterExport(contentUri:Uri){
        val intent= Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
//            putExtra("CONTENT_TYPE","text/csv")
            data = contentUri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }



    @SuppressLint("MissingSuperCall")
    @Throws(IOException::class)
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if(requestCode == PICK_CSV_FOR_IMPORT && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also {
                contentResolver.openInputStream(it)?.use {inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use{ reader ->
                        val allLines=reader.readLines()
                        if(checkFile(allLines))
                        {
                            (application as FoodOMatApplication).applicationScope.launch {
                                importMeals(allLines)}
                        }
                        else Toast.makeText(applicationContext, "Ungültige Formatierung", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(requestCode == CREATE_CSV_FOR_EXPORT && resultCode == Activity.RESULT_OK){
            resultData?.data?.also { uri ->
                (application as FoodOMatApplication).applicationScope.launch {
                    exportMeals(uri)}
            }
        }
    }

    private suspend fun exportMeals(uri: Uri){
            try {
                contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fileOutputStream ->
                        val allMealsToExport=viewModel.getMealsAsList()
                        allMealsToExport.forEach { meal-> fileOutputStream.write("$meal\n".toByteArray())}
                        fileOutputStream.close()
                        withContext(Dispatchers.Main){
                            Toast.makeText(applicationContext, "${allMealsToExport.size} Mahlzeiten erfolgreich exportiert", Toast.LENGTH_SHORT).show()
                        }
//                        shareFileAfterExport(uri)
                    }
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

    }

    private fun checkFile(allLines: List<String>): Boolean {
        allLines.forEach { line ->
            if(line.isNotEmpty() && line.count{ char -> char==';'} != 8) return false
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle selection of menu item
        when (item.itemId) {
            R.id.settings -> {
                navController.navigate(R.id.settingsFragment)
                return true
            }
            R.id.importMeals -> {
                openFileForMealImport()
                return true
            }
            R.id.exportMeals -> {
                createFileForMealExport()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
         menuInflater.inflate(R.menu.top_bar_menu, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
