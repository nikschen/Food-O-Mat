package com.fourquestionmarks.food_o_mat.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.adapter.MealListAdapter
import com.fourquestionmarks.food_o_mat.databinding.FragmentDashboardBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.fourquestionmarks.food_o_mat.ui.MealViewModel
import com.fourquestionmarks.food_o_mat.ui.MealViewModelFactory
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * [Fragment] that displays a random [Meal] of the day
 *
 * */
class DashboardFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private lateinit var meal: Meal
    private lateinit var allMeals: List<Meal>

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel= MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao()).create(MealViewModel::class.java)
        //gets all Meals to get all meals and select a random meal for displaying
        viewModel.allMeals.observe(this.viewLifecycleOwner) { listOfMeals ->
            allMeals=listOfMeals
            if(allMeals.isNotEmpty())
            {
                val randomID:Int=Random.nextInt(0,allMeals.size)
                meal=allMeals[randomID]
                bind(meal)
                binding.mealCardInclude.mealCard.setOnClickListener {
                    val action = DashboardFragmentDirections.actionNavigationDashboardToMealDetailFragment(meal.name, meal.ID!!)
                    findNavController().navigate(action)
                }
            }
        }
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in [Meal] data.
     */
    private fun bind(meal: Meal) {
        binding.mealCardInclude.apply {
            val df = DecimalFormat("#.##")
            df.roundingMode= RoundingMode.HALF_UP
            name.text=meal.name
            category.text=meal.category
            calories.text=getString(R.string.caloriesWithUnit, meal.calories.roundToInt())
            carbohydrates.text=getString(R.string.nutritionsWithUnit,df.format(meal.carbohydrates))
            proteins.text=getString(R.string.nutritionsWithUnit,df.format(meal.proteins))
            fats.text=getString(R.string.nutritionsWithUnit,df.format(meal.fats))
            isVeggieCheckbox.isChecked=meal.isVeggie
            isVeganCheckbox.isChecked=meal.isVegan
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // addMealButton to navigate to addOrUpdateModuleFragment to add new modules
        binding.addMealButton.setOnClickListener {
            val action = DashboardFragmentDirections.actionNavigationDashboardToAddOrUpdateMealFragment(getString(R.string.title_new_meal),0)
            findNavController().navigate(action)
        }
    }
}
