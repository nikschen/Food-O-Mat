package com.fourquestionmarks.food_o_mat.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.FragmentAddOrUpdateIngredientDialogBinding
import com.fourquestionmarks.food_o_mat.model.Ingredient
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AddOrUpdateIngredientDialogFragment : DialogFragment() {

    private val ingredientViewModel: IngredientViewModel by activityViewModels {IngredientViewModelFactory((activity?.application as FoodOMatApplication).database.ingredientDao())}
    private var newIngredientNameLabel: TextInputLayout? = null
    private var newIngredientName: EditText? = null
    private var newIngredientCaloriesLabel: TextInputLayout? = null
    private var newIngredientCalories: EditText? = null
    private var newIngredientCarbohydratesLabel: TextInputLayout? = null
    private var newIngredientCarbohydrates: EditText? = null
    private var newIngredientFatsLabel: TextInputLayout? = null
    private var newIngredientFats: EditText? = null
    private var newIngredientProteinsLabel: TextInputLayout? = null
    private var newIngredientProteins: EditText? = null
    private var searchForIngredientButton: ImageButton? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Inflate and set the layout for the dialog
            val view= layoutInflater.inflate(R.layout.fragment_add_or_update_ingredient_dialog, null)


            //store views into variables to change them quickly
            newIngredientNameLabel=view.findViewById<TextInputLayout>(R.id.newIngredientNameLabel)
            newIngredientName=view.findViewById<EditText>(R.id.newIngredientName)
            newIngredientCaloriesLabel=view.findViewById<TextInputLayout>(R.id.newIngredientCaloriesLabel)
            newIngredientCalories=view.findViewById<EditText>(R.id.newIngredientCalories)
            newIngredientCarbohydratesLabel=view.findViewById<TextInputLayout>(R.id.newIngredientCarbohydratesLabel)
            newIngredientCarbohydrates=view.findViewById<EditText>(R.id.newIngredientCarbohydrates)
            newIngredientFatsLabel=view.findViewById<TextInputLayout>(R.id.newIngredientFatsLabel)
            newIngredientFats=view.findViewById<EditText>(R.id.newIngredientFats)
            newIngredientProteinsLabel=view.findViewById<TextInputLayout>(R.id.newIngredientProteinsLabel)
            newIngredientProteins=view.findViewById<EditText>(R.id.newIngredientProteins)
            searchForIngredientButton=view.findViewById<ImageButton>(R.id.searchForIngredientButton)

            // add behaviour to the search button inside the dialog
            searchForIngredientButton!!.setOnClickListener {

                    val name=newIngredientName!!.text.toString()
                    if(name.isNotEmpty()) {
                        newIngredientNameLabel!!.error=null
                        var ingredient: Ingredient? =null
                        newIngredientNameLabel!!.error="Zutat nicht in Datenbank"

                        lifecycleScope.launch {
                            val getLowestScore = async(Dispatchers.IO) {
                                ingredient=ingredientViewModel.getIngredientByName(name)
                                if(ingredient!=null)
                                {
                                    newIngredientCalories!!.setText(ingredient!!.calories.toString())
                                    newIngredientCarbohydrates!!.setText(ingredient!!.carbohydrates.toString())
                                    newIngredientFats!!.setText(ingredient!!.fats.toString())
                                    newIngredientProteins!!.setText(ingredient!!.proteins.toString())
                                }
                            }
                            getLowestScore.await() // wait for result of I/O operation without blocking the main thread
                            if(ingredient!=null) newIngredientNameLabel!!.error=null
                        }

                    }
                    else
                    {
                        newIngredientNameLabel!!.error = "Name der Zutat fehlt"
                    }

                }

            // Pass null as the parent view because its going in the dialog layout
            builder .setView(view)
                    .setTitle(R.string.add_new_ingredient)
                    .setPositiveButton(R.string.save_ingredient_button_text) { dialog, _ ->

                        if(isValid())
                        {
                            saveIngredient()
                            dialog.dismiss()
                        }

                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() } // User cancelled the dialog


            // Create the AlertDialog object and return it
            builder.create()



        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid=true
        if(newIngredientName!!.text.isBlank())
        {
            newIngredientNameLabel!!.error="Name der Zutat fehlt"
            valid=false
        }
        else newIngredientNameLabel!!.error=null
        if(newIngredientCalories!!.text.isBlank())
        {
            newIngredientCaloriesLabel!!.error="Kalorien fehlen"
            valid=false
        }
        else if(newIngredientCalories!!.text.toString().toIntOrNull()==null)
        {
            newIngredientCaloriesLabel!!.error="Ungültiges Zahlenformat"
            valid=false
        }
        else newIngredientCaloriesLabel!!.error=null
        if(newIngredientCarbohydrates!!.text.isBlank())
        {
            newIngredientCarbohydratesLabel!!.error="Kohlenhydrate fehlen"
            valid=false
        }
        else if(newIngredientCarbohydrates!!.text.toString().replace(',','.').toFloatOrNull()==null)
        {
            newIngredientCarbohydratesLabel!!.error="Ungültiges Zahlenformat"
            valid=false
        }
        else newIngredientCarbohydratesLabel!!.error=null
        if(newIngredientFats!!.text.isBlank())
        {
            newIngredientFatsLabel!!.error="Fett fehlt"
            valid=false
        }
        else if(newIngredientCarbohydrates!!.text.toString().replace(',','.').toFloatOrNull()==null)
        {
            newIngredientFatsLabel!!.error="Ungültiges Zahlenformat"
            valid=false
        }
        else newIngredientFatsLabel!!.error=null
        if(newIngredientProteins!!.text.isBlank())
        {
            newIngredientProteinsLabel!!.error="Proteine fehlen"
            valid=false
        }
        else if(newIngredientCarbohydrates!!.text.toString().replace(',','.').toFloatOrNull()==null)
        {
            newIngredientProteinsLabel!!.error="Ungültiges Zahlenformat"
            valid=false
        }
        else newIngredientProteinsLabel!!.error=null


        return valid
    }

    private fun saveIngredient()
    {
        val ingredient=Ingredient(  name=newIngredientName!!.text.toString(),
                                    calories = newIngredientCalories!!.text.toString().toInt(),
                                    carbohydrates = newIngredientCarbohydrates!!.text.toString().replace(',','.').toFloat(),
                                    fats = newIngredientFats!!.text.toString().replace(',','.').toFloat(),
                                    proteins = newIngredientProteins!!.text.toString().replace(',','.').toFloat(),
                                    mealID = -1)
        ingredientViewModel.insertIngredient(ingredient)
    }

}