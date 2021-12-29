package com.fourquestionmarks.food_o_mat.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.adapter.MealListAdapter
import com.fourquestionmarks.food_o_mat.databinding.FragmentMealListBinding
import com.fourquestionmarks.food_o_mat.databinding.FragmentSearchBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.fourquestionmarks.food_o_mat.ui.MealDetailFragmentArgs
import com.fourquestionmarks.food_o_mat.ui.MealViewModel
import com.fourquestionmarks.food_o_mat.ui.MealViewModelFactory


class MealListFragment : Fragment() {
    private val navigationArgs: MealListFragmentArgs by navArgs()
    private lateinit var viewModel: MealViewModel
    private var _binding: FragmentMealListBinding? = null
    private val binding get() = _binding!!
    private lateinit var validMeals: List<Meal>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel= MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao()).create(MealViewModel::class.java)
        _binding = FragmentMealListBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //sets adapter to the ModuleListAdapter, specially created to list all Modules with only some data
        val adapter = MealListAdapter {
            //add action to each module card that navigates to the detail view of the module
            val action =
                MealListFragmentDirections.actionMealListFragmentToMealDetailFragment(it.name,it.ID!!) // id of the module as argument to get correct detail view data
            this.findNavController().navigate(action)
        }

        binding.mealList.layoutManager = LinearLayoutManager(this.context)
        binding.mealList.adapter = adapter

        viewModel.allMeals.observe(this.viewLifecycleOwner) { listOfMeals ->
            validMeals=listOfMeals.filter{meal -> navigationArgs.resultMealIDs.contains(meal.ID!!)}
            adapter.submitList(validMeals)
        }
    }

}