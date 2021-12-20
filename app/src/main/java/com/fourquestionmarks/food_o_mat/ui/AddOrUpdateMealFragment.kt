package com.fourquestionmarks.food_o_mat.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.databinding.FragmentAddOrUpdateMealBinding
import com.fourquestionmarks.food_o_mat.model.Meal


class AddOrUpdateMealFragment : Fragment() {
    private val viewModel: MealViewModel by activityViewModels {
        MealViewModel.MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao())
    }
    private val navigationArgs: AddOrUpdateMealFragmentArgs by navArgs()

    lateinit var meal: Meal

    private var _binding: FragmentAddOrUpdateMealBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrUpdateMealBinding.inflate(inflater, container, false)
        return binding.root
    }



    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isValid(): Boolean {
        return viewModel.isValid(
            binding.mealName.text.toString(),
            binding.category.text.toString(),
            binding.calories.text.toString(),
        )
    }

    /**
     * Binds views with the passed in [Meal] information.
     */
    private fun bind(meal: Meal) {
        binding.apply {
            mealName.setText(meal.name, TextView.BufferType.SPANNABLE)
            category.setText(meal.category, TextView.BufferType.SPANNABLE)
            calories.setText(meal.calories.toString(), TextView.BufferType.SPANNABLE)
            carbohydrates.setText(meal.carbohydrates.toString(), TextView.BufferType.SPANNABLE)
            proteins.setText(meal.proteins.toString(), TextView.BufferType.SPANNABLE)
            fats.setText(meal.fats.toString(), TextView.BufferType.SPANNABLE)
            saveMealButton.setOnClickListener { updateMeal() }
        }
    }

    /**
     * Inserts the new Meal into database and navigates up to list fragment.
     */
    private fun addNewMeal() {
        if (isValid()) {
            viewModel.insertMeal(
                Meal(
                    ID=null,
                    name=binding.mealName.toString(),
                    category=binding.category.toString(),
                    calories=binding.calories.toString().toFloat(),
                    carbohydrates=binding.carbohydrates.toString().toFloat(),
                    proteins=binding.proteins.toString().toFloat(),
                    fats=binding.fats.toString().toFloat(),
                    isVeggie=binding.veggieSwitch.isChecked,
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
     * Updates an existing Meal in the database and navigates up to list fragment.
     */
    private fun updateMeal() {
        if (isValid()) {
            viewModel.updateMeal(
                Meal(
                    name=binding.mealName.toString(),
                    category=binding.category.toString(),
                    calories=binding.calories.toString().toFloat(),
                    carbohydrates=binding.carbohydrates.toString().toFloat(),
                    proteins=binding.proteins.toString().toFloat(),
                    fats=binding.fats.toString().toFloat(),
                    isVeggie=binding.veggieSwitch.isChecked,
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