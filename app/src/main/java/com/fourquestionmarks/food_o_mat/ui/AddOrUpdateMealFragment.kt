package com.fourquestionmarks.food_o_mat.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.FragmentAddOrUpdateMealBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class AddOrUpdateMealFragment : Fragment() {
    private val viewModel: MealViewModel by activityViewModels {MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao())}
    private val navigationArgs: AddOrUpdateMealFragmentArgs by navArgs()
    private lateinit var allCategories: List<String>
    lateinit var meal: Meal

    private var _binding: FragmentAddOrUpdateMealBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrUpdateMealBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val operation = async(Dispatchers.IO) {
                viewModel.getAllCategories().let {
                    allCategories=it
                }
            }
            operation.await() // wait for result of I/O operation without blocking the main thread
            val items = allCategories
            val adapter= ArrayAdapter(requireContext(),  R.layout.dropdown_list_item, items)
            binding.category.setAdapter(adapter)

        }
        return binding.root
    }



    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isValid(param: View?, checkAll: Boolean=false): Boolean {
        var isValid=true

        with (binding)
        {

            if((param==mealName||checkAll) && mealName.text.isNullOrEmpty())
            {
                mealNameLabel.error = "Name fehlt"
                isValid=false
            }
            else mealNameLabel.error=null



            if((param==category||checkAll) && category.text.isNullOrEmpty())
            {
                categoryLabel.error = "Kategorie fehlt"
                isValid=false
            }
            else categoryLabel.error=null



            if(param==calories||checkAll)
            {
                when {
                    calories.text.isNullOrEmpty() ->
                    {
                        caloriesLabel.error = "Kalorien fehlen"
                        isValid = false
                    }
                    calories.text.toString().toFloatOrNull() == null ->
                    {
                        caloriesLabel.error = "Ungültiges Zahlenformat"
                        isValid = false
                    }
                    calories.text.toString().toFloat() > 1500.0f -> caloriesLabel.error = "Sehr hohe Kalorien. Vielleicht ein Fehler?"
                }

            }
            else caloriesLabel.error=null

            if(param==carbohydrates||checkAll)
            {
                when {
                    carbohydrates.text.isNullOrEmpty() ->
                    {
                        carbohydratesLabel.error = "Kohlenhydrate fehlen"
                        isValid = false
                    }
                    carbohydrates.text.toString().toFloatOrNull() == null ->
                    {
                        carbohydratesLabel.error = "Ungültiges Zahlenformat"
                        isValid = false
                    }
                    else -> carbohydratesLabel.error = null
                }
            }

            if(param==proteins||checkAll) {
                when {
                    proteins.text.isNullOrEmpty() -> {
                        proteinsLabel.error = "Eiweiß fehlt"
                        isValid = false
                    }
                    proteins.text.toString().toFloatOrNull() == null -> {
                        proteinsLabel.error = "Ungültiges Zahlenformat"
                        isValid = false
                    }
                    else -> proteinsLabel.error = null
                }
            }


            if(param==fats||checkAll) {
                when {
                    fats.text.isNullOrEmpty() -> {
                        fatsLabel.error = "Fett fehlt"
                        isValid = false
                    }
                    fats.text.toString().toFloatOrNull() == null -> {
                        fatsLabel.error = "Ungütliges Zahlenformat"
                        isValid = false
                    }
                    else -> fatsLabel.error = null
                }
            }

        }
        return isValid
    }

    /**
     * Binds views with the passed in [Meal] information.
     */
    private fun bind(meal: Meal) {
        binding.apply {
            mealName.setText(meal.name, TextView.BufferType.SPANNABLE)
            category.setText(meal.category, false);
            calories.setText(meal.calories.toString(), TextView.BufferType.SPANNABLE)
            carbohydrates.setText(meal.carbohydrates.toString(), TextView.BufferType.SPANNABLE)
            proteins.setText(meal.proteins.toString(), TextView.BufferType.SPANNABLE)
            fats.setText(meal.fats.toString(), TextView.BufferType.SPANNABLE)
            veggieCheckbox.isChecked=meal.isVeggie
            veganCheckbox.isChecked=meal.isVegan
            saveMealButton.setOnClickListener { updateMeal() }
        }
    }



    /**
     * Inserts the new Meal into database and navigates up to list fragment.
     */
    private fun addNewMeal() {
        if (isValid(null,true)) {
            viewModel.insertMeal(
                Meal(
                    ID=null,
                    name=binding.mealName.text.toString(),
                    category=binding.category.text.toString(),
                    calories=binding.calories.text.toString().replace(',','.').toFloat().roundToInt().toFloat(),
                    carbohydrates=binding.carbohydrates.text.toString().replace(',','.').toFloat(),
                    proteins=binding.proteins.text.toString().replace(',','.').toFloat(),
                    fats=binding.fats.text.toString().replace(',','.').toFloat(),
                    isVeggie=binding.veggieCheckbox.isChecked,
                    isVegan=binding.veganCheckbox.isChecked,
                )
            )
            viewModel.getLastInsertedMeal().observe(this.viewLifecycleOwner) { selectedMeal ->
                meal = selectedMeal
                bind(meal)
                val action = AddOrUpdateMealFragmentDirections.actionAddOrUpdateMealFragmentToMealDetailFragment(meal.name, meal.ID!!)
                findNavController().navigate(action)
            }
        }
    }

    /**
     * Updates an existing Meal in the database and navigates to MealDetailFragment of the updated meal.
     */
    private fun updateMeal() {
        if (isValid(null, true)) {
            viewModel.updateMeal(
                Meal(
                    name=binding.mealName.text.toString(),
                    category=binding.category.text.toString(),
                    calories=binding.calories.text.toString().replace(',','.').toFloat().roundToInt().toFloat(),
                    carbohydrates=binding.carbohydrates.text.toString().replace(',','.').toFloat(),
                    proteins=binding.proteins.text.toString().replace(',','.').toFloat(),
                    fats=binding.fats.text.toString().replace(',','.').toFloat(),
                    isVeggie=binding.veggieCheckbox.isChecked,
                    isVegan=binding.veganCheckbox.isChecked,
                )
            )

            val action = AddOrUpdateMealFragmentDirections.actionAddOrUpdateMealFragmentToMealDetailFragment(meal.name, meal.ID!!)
            findNavController().navigate(action)
        }
    }

    /**
     * Called when the view is created.
     * The MealId Navigation argument determines the edit Meal  or add new Meal.
     * If the MealId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding)
        {
            mealName.doAfterTextChanged {isValid(mealName) }
            category.doAfterTextChanged {isValid(category) }
            calories.doAfterTextChanged {isValid(calories) }
            carbohydrates.doAfterTextChanged {isValid(carbohydrates) }
            proteins.doAfterTextChanged {isValid(proteins) }
            fats.doAfterTextChanged {isValid(fats) }



            //an onClickListener that checks if veggieCheckbox is ticked, otherwise veganCheckbox is unticked because of logical reasons
            veggieCheckbox.setOnClickListener{
                if(!veggieCheckbox.isChecked) veganCheckbox.isChecked=false
            }

            //an onClickListener that checks if veganCheckbox is ticked and if so, veggieCheckbox is ticked too because of logical reasons
            veganCheckbox.setOnClickListener{
                if(veganCheckbox.isChecked) veggieCheckbox.isChecked=true
            }
        }




        val id = navigationArgs.mealID
        if (id > 0) {
            viewModel.getMealById(id).observe(this.viewLifecycleOwner) { selectedMeal ->
                meal = selectedMeal
                bind(meal)
            }
        } else {
            binding.saveMealButton.setOnClickListener {
                addNewMeal()
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}