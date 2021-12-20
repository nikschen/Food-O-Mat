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
import com.fourquestionmarks.food_o_mat.ui.MealViewModel

class DashboardFragment : Fragment() {

    private val viewModel: MealViewModel by activityViewModels {
        MealViewModel.MealViewModelFactory(
            (activity?.application as FoodOMatApplication).database.mealDao()
        )
    }

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //set binding to current fragment binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets adapter to the MealListAdapter, specially created to list all Meals with only some data (name, calories, isVeggie)
        val adapter = MealListAdapter {
            //add action to each module card that navigates to the detail view of the module
//            val action =
//                DashboardFragmentDirections.actionNavigationDashboardToModuleDetailFragment(it.moduleName,it.id!!) // id of the module as argument to get correct detail view data
//            this.findNavController().navigate(action)
        }

        binding.mealList.layoutManager = LinearLayoutManager(this.context)
        binding.mealList.adapter = adapter

        // observer on the allDashboard list to update the UI automatically when the data changes.
        viewModel.allMeals.observe(this.viewLifecycleOwner) { Meal ->
            Meal.let {
                adapter.submitList(it)
            }
        }

        // saveMealButton to navigate to addOrUpdateModuleFragment to add new modules
        binding.addMealButton.setOnClickListener {
//            val action = DashboardFragmentDirections.actionNavigationDashboardToNewModuleFragment(getString(
//                R.string.title_new_module),0)
//            findNavController().navigate(action)
        }
    }
}
