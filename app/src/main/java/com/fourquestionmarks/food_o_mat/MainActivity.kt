package com.fourquestionmarks.food_o_mat

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
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
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

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

        settings=(application as FoodOMatApplication).settings
        viewModel= MealViewModelFactory((application as FoodOMatApplication).database.mealDao()).create(MealViewModel::class.java)

        val navView: BottomNavigationView = binding.navView

        if(settings.getBoolValue("isFirstRun"))
        {
            val inputStream: InputStream = resources.openRawResource(R.raw.mahlzeiten)
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            reader.readLines().forEach {

                //get a string array of all items in this lin
                val mealData = it.split(";").toMutableList()
                //turn 'möglich' and 'ja' into true and 'nein' into false
                if(mealData[6]=="ja" ||mealData[6]=="möglich") mealData[6]="true" else mealData[6]="false"
                if(mealData[7]=="ja" ||mealData[7]=="möglich") mealData[7]="true" else mealData[7]="false"
                // generate new meal entry
                val meal= Meal( null,                                                        //ID
                                    mealData[0],                                                 // meal name
                                    mealData[1],                                                 // category
                                    mealData[2].replace(",",".").toFloat(),     //calories
                                    mealData[3].replace(",",".").toFloat(),     //carbohydrates
                                    mealData[4].replace(",",".").toFloat(),     //proteins
                                    mealData[5].replace(",",".").toFloat(),     //fats
                                    mealData[6].toBoolean(), //isVeggie
                                    mealData[7].toBoolean()) //isVegan

                //insert new meal entry
                viewModel.insertMeal(meal)
            }
            settings.writeBoolValue("isFirstRun",false)
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
