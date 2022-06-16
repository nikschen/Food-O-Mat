package com.fourquestionmarks.food_o_mat.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.FragmentAddOrUpdateMealViaIngredientsBinding
import com.fourquestionmarks.food_o_mat.model.Ingredient
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.w3c.dom.Text

/**
 * [Fragment] to create a new [Meal] entry for the database according to user input
 * requires a name, a category, calories and nutritions of the meal
 * */
class AddOrUpdateMealViaIngredientsFragment : Fragment() {
    private val viewModel: MealViewModel by activityViewModels {MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao())}
    private val ingredientViewModel: IngredientViewModel by activityViewModels {IngredientViewModelFactory((activity?.application as FoodOMatApplication).database.ingredientDao())}
    private val navigationArgs: AddOrUpdateMealViaIngredientsFragmentArgs by navArgs()
    private lateinit var allCategories: List<String>
    lateinit var meal: Meal

    private var _binding: FragmentAddOrUpdateMealViaIngredientsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrUpdateMealViaIngredientsBinding.inflate(inflater, container, false)

        //gets all categories for category dropdown
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

        ingredientViewModel.getIngredientsByMealId(-1).observe(this.viewLifecycleOwner){ ingredientsForCurrentMeal ->
            ingredientsForCurrentMeal.let {
                binding.ingredientsList.ingredientsList.adapter. //TODO: adapter schreiben für ingredients
            }
        }

        return binding.root
    }



    /**
     * Validates all input params to check for empty/null and validity
     */
    private fun isValid(param: View?, checkAll: Boolean=false): Boolean {
        var isValid=true

        with (binding)
        {

            if((param==mealName||checkAll) && mealName.text.isNullOrEmpty())
            {
                mealNameLabel.error = getString(R.string.nameMissingError)
                isValid=false
            }
            else mealNameLabel.error=null



            if((param==category||checkAll) && category.text.isNullOrEmpty())
            {
                categoryLabel.error = getString(R.string.categoryMissingError)
                isValid=false
            }
            else categoryLabel.error=null

        }
        return isValid
    }

    /**
     * Binds views with the passed in [Meal] information.
     */
    private fun bind(meal: Meal) {
        binding.apply {
            mealName.setText(meal.name, TextView.BufferType.SPANNABLE)
            category.setText(meal.category, false)
            veggieCheckbox.isChecked=meal.isVeggie
            veganCheckbox.isChecked=meal.isVegan
            saveMealButton.setOnClickListener { updateMeal() }
        }
    }



    /**
     * Inserts the new Meal into database and navigates to detail fragment of the inserted meal
     */
    private fun addNewMeal() {
        if (isValid(null,true)) {
            viewModel.insertMeal(
                Meal(
                    ID=null,
                    name=binding.mealName.text.toString(),
                    category=binding.category.text.toString(),
                    calories=binding.mealCardWithNutritions.mealCalories.text.toString().toInt(),
                    carbohydrates=binding.mealCardWithNutritions.mealCarbohydrates.text.toString().replace(',','.').toFloat(),
                    proteins=binding.mealCardWithNutritions.mealProteins.text.toString().replace(',','.').toFloat(),
                    fats=binding.mealCardWithNutritions.mealFats.text.toString().replace(',','.').toFloat(),
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
                    ID=meal.ID,
                    name=binding.mealName.text.toString(),
                    category=binding.category.text.toString(),
                    calories=binding.mealCardWithNutritions.mealCalories.text.toString().toInt(),
                    carbohydrates=binding.mealCardWithNutritions.mealCarbohydrates.text.toString().replace(',','.').toFloat(),
                    proteins=binding.mealCardWithNutritions.mealProteins.text.toString().replace(',','.').toFloat(),
                    fats=binding.mealCardWithNutritions.mealFats.text.toString().replace(',','.').toFloat(),
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
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding)
        {
            mealName.doAfterTextChanged {isValid(mealName) }
            category.doAfterTextChanged {isValid(category) }

            //an onClickListener that checks if veggieCheckbox is ticked, otherwise veganCheckbox is unticked because of logical reasons
            veggieCheckbox.setOnClickListener{
                if(!veggieCheckbox.isChecked) veganCheckbox.isChecked=false
            }

            //an onClickListener that checks if veganCheckbox is ticked and if so, veggieCheckbox is ticked too because of logical reasons
            veganCheckbox.setOnClickListener{
                if(veganCheckbox.isChecked) veggieCheckbox.isChecked=true
            }

            addIngredientButton.setOnClickListener{
                val newDialog=AddOrUpdateIngredientDialogFragment()
                newDialog.show(childFragmentManager,"ingredientDialog")
            }


        }


        //check if mealID in navArgs is a real ID to determine if add or update of a meal should be done

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
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}