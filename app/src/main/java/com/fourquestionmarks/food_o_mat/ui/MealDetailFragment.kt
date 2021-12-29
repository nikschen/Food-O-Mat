package com.fourquestionmarks.food_o_mat.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.FragmentMealDetailBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.roundToInt

/**
 * A fragment representing a list of Items.
 */
class MealDetailFragment : Fragment() {
    private val navigationArgs: MealDetailFragmentArgs by navArgs()
    lateinit var meal: Meal

    private val viewModel: MealViewModel by activityViewModels {MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao())}

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(meal: Meal) {
        binding.apply {
            category.setText(meal.category, TextView.BufferType.SPANNABLE)
            calories.setText(meal.calories.roundToInt().toString(), TextView.BufferType.SPANNABLE)
            carbohydrates.setText(meal.carbohydrates.toString(), TextView.BufferType.SPANNABLE)
            proteins.setText(meal.proteins.toString(), TextView.BufferType.SPANNABLE)
            fats.setText(meal.fats.toString(), TextView.BufferType.SPANNABLE)
            isVeggieCheckbox.isChecked=meal.isVeggie
            isVeganCheckbox.isChecked=meal.isVegan
            deleteMeal.setOnClickListener { showConfirmationDialog() }
            editMeal.setOnClickListener { editMeal() }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editMeal() {
        val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddOrUpdateMealFragment(meal.name,meal.ID!!)
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_question,meal.name))
            .setCancelable(true)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteMeal()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteMeal() {
        viewModel.deleteMeal(meal)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.mealID
        viewModel.getMealById(id).observe(this.viewLifecycleOwner) { selectedMeal ->
            meal = selectedMeal
            bind(meal)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}