package com.fourquestionmarks.food_o_mat.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.fourquestionmarks.food_o_mat.FoodOMatApplication
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.FragmentSearchBinding
import com.fourquestionmarks.food_o_mat.model.Meal
import com.fourquestionmarks.food_o_mat.ui.MealViewModel
import com.fourquestionmarks.food_o_mat.ui.MealViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

import android.view.inputmethod.InputMethodManager
import android.widget.TextView


/**
 * [Fragment] that displays one random [Meal] according to user defined params if provided or any meal of the database if no params given
 * implements an input field for a specific name or part of a name, a Rangeslider for a range of calories, as well as veggie and vegan checkboxes and selectable categories
 * */
class SearchFragment : Fragment() {

    private lateinit var viewModel: MealViewModel
    private var lowestCalorieScore: Float = 0.0f
    private var highestCalorieScore: Float = 1500.0f
    private var lowestPossibleCalorieScore: Float = 0.0f
    private var highestPossibleCalorieScore: Float = 1500.0f
    private lateinit var allCategories: List<String>
    private var wantedCategories: List<String> = mutableListOf<String>()
    private var initialTickedCategories:BooleanArray=BooleanArray(0)
    private var newValueIsValid=false

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel= MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao()).create(MealViewModel::class.java)
        binding.caloriesSlider.valueFrom=lowestPossibleCalorieScore
        binding.caloriesSlider.valueTo=highestPossibleCalorieScore

        //get lowest and highest calorie score out of the database
        lifecycleScope.launch {
            val getLowestScore = async(Dispatchers.IO) {
                viewModel.getLowestCalorieScore().let {
                    lowestCalorieScore=it
                }
            }
            val getHighestScore = async(Dispatchers.IO) {
                viewModel.getHighestCalorieScore().let {
                    highestCalorieScore=it
                }
            }
            getLowestScore.await() // wait for result of I/O operation without blocking the main thread
            getHighestScore.await() // wait for result of I/O operation without blocking the main thread
            //set lowest and highest calorie scores as default values on the rangeslider and direct inputs
            binding.caloriesSlider.setValues(lowestCalorieScore.roundToInt().toFloat(),highestCalorieScore.roundToInt().toFloat())
            binding.minCaloriesDirectInput.setText(lowestCalorieScore.roundToInt().toString())
            binding.maxCaloriesDirectInput.setText(highestCalorieScore.roundToInt().toString())
        }

        //get all available cateogies out of the database for the section pop up
        lifecycleScope.launch {
            val operation = async(Dispatchers.IO) {
                viewModel.getAllCategories().let {
                    allCategories=it
                }
            }
            operation.await() // wait for result of I/O operation without blocking the main thread

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mealNameInput.setOnEditorActionListener(TextView.OnEditorActionListener{ _, actionId, _ ->
            if(actionId== EditorInfo.IME_ACTION_SEARCH) {
                startSearch()
                return@OnEditorActionListener true
            }
            false
        })

        binding.getMealButton.isEnabled=true

        //on change listener for only user inferred changes to the rangeslider values, copies the changes to the corresponding direct inputs
        binding.caloriesSlider.addOnChangeListener { slider, values, fromUser ->

            if(fromUser)
            {
                binding.minCaloriesDirectInput.setText(slider.values[0].roundToInt().toString())
                binding.maxCaloriesDirectInput.setText(slider.values[1].roundToInt().toString())
                checkParams()
            }
        }

        //an on text change listener that validates the input before letting the text change happen to ensure valid values and no crashes
        binding.minCaloriesDirectInput.doOnTextChanged { value, _, _, _ ->

            when {
                value.isNullOrEmpty() -> { //filter for null or empty values
                    newValueIsValid=false
                    binding.getMealButton.isEnabled=false
                    binding.caloriesError.text = getString(R.string.caloriesEmptyError)
                    binding.caloriesError.visibility=View.VISIBLE
                    binding.minCaloriesDirectInput.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                }
                value.toString().toFloatOrNull() == null -> { //filter for not valid float values
                    newValueIsValid=false
                    binding.getMealButton.isEnabled=false
                    binding.caloriesError.text = getString(R.string.caloriesFormatError)
                    binding.minCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                    binding.caloriesError.visibility=View.VISIBLE

                }
                else -> {
                    val actualFloatValue=value.toString().toFloat().roundToInt().toFloat()
                    when {
                        actualFloatValue<lowestPossibleCalorieScore -> { //filter for too low values
                            newValueIsValid=false
                            binding.getMealButton.isEnabled=false
                            binding.caloriesError.text = getString(R.string.caloriesToLowError)
                            binding.minCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                            binding.caloriesError.visibility=View.VISIBLE
                        }
                        actualFloatValue>highestPossibleCalorieScore -> { //filter for too high values
                            newValueIsValid=false
                            binding.getMealButton.isEnabled=false
                            binding.caloriesError.text = getString(R.string.caloriesToHighError)
                            binding.minCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                            binding.caloriesError.visibility=View.VISIBLE
                        }
                        else -> {
                            newValueIsValid=true
                            binding.caloriesError.visibility=View.INVISIBLE
                            binding.minCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor))
                        }
                    }
                }
            }
        }

        //an on text change listener that validates the input before letting the text change happen to ensure valid values and no crashes
        binding.maxCaloriesDirectInput.doOnTextChanged { value, _, _, _ ->
            when {
                value.isNullOrEmpty() -> { //filter for null or empty values
                    newValueIsValid=false
                    binding.getMealButton.isEnabled=false
                    binding.caloriesError.text = getString(R.string.caloriesEmptyError)
                    binding.caloriesError.visibility=View.VISIBLE
                    binding.maxCaloriesDirectInput.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                }
                value.toString().toFloatOrNull() == null -> { //filter for not valid float values
                    newValueIsValid=false
                    binding.getMealButton.isEnabled=false
                    binding.caloriesError.text = getString(R.string.caloriesFormatError)
                    binding.maxCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                    binding.caloriesError.visibility=View.VISIBLE

                }
                else -> {
                    val actualFloatValue=value.toString().toFloat().roundToInt().toFloat()
                    when {
                        actualFloatValue>highestPossibleCalorieScore -> { //filter for too high values
                            newValueIsValid=false
                            binding.getMealButton.isEnabled=false
                            binding.caloriesError.text = getString(R.string.caloriesToHighError)
                            binding.maxCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                            binding.caloriesError.visibility=View.VISIBLE
                        }
                        actualFloatValue<lowestPossibleCalorieScore -> { //filter for too low values
                            newValueIsValid=false
                            binding.getMealButton.isEnabled=false
                            binding.caloriesError.text = getString(R.string.caloriesToLowError)
                            binding.maxCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_error))
                            binding.caloriesError.visibility=View.VISIBLE
                        }
                        else -> {
                            newValueIsValid=true
                            binding.caloriesError.visibility=View.INVISIBLE
                            binding.maxCaloriesDirectInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor))
                        }
                    }
                }
            }
        }


        //an after text change listener that copies the value to the rangeslider
        binding.minCaloriesDirectInput.doAfterTextChanged { value ->

            val actualValue= value.toString().toFloatOrNull()?.roundToInt()?.toFloat()
            if(binding.minCaloriesDirectInput.hasFocus() && newValueIsValid)
            {
                binding.caloriesSlider.setValues(actualValue,binding.caloriesSlider.values[1])
                checkParams()
            }
        }

        //an after text change listener that copies the value to the rangeslider
        binding.maxCaloriesDirectInput.doAfterTextChanged { value ->
            val actualValue= value.toString().toFloatOrNull()?.roundToInt()?.toFloat()
            if(binding.maxCaloriesDirectInput.hasFocus() && newValueIsValid)
            {
                binding.caloriesSlider.setValues(binding.caloriesSlider.values[0],actualValue)
                checkParams()
            }
        }

        //an onClickListener that checks if veggieCheckbox is ticked, otherwise veganCheckbox is unticked because of logical reasons
        binding.veggieCheckbox.setOnClickListener{
            if(!binding.veggieCheckbox.isChecked) binding.veganCheckbox.isChecked=false
        }

        //an onClickListener that checks if veganCheckbox is ticked and if so, veggieCheckbox is ticked too because of logical reasons
        binding.veganCheckbox.setOnClickListener{
            if(binding.veganCheckbox.isChecked) binding.veggieCheckbox.isChecked=true
        }

        //trigger for the category selection popup
        binding.categorySelectionTrigger.setOnClickListener {chooseCategoryDialog()}
        //button to start the search
        binding.getMealButton.setOnClickListener {startSearch()}
    }

    /**
     * creates dialog for selection of categories, if none is ticked, all categories will be considered
     */
    private fun chooseCategoryDialog()
    {
        val categories = allCategories.toTypedArray()
        var choicesInitial = BooleanArray(allCategories.size)
        if(initialTickedCategories.isNotEmpty()) choicesInitial=initialTickedCategories
        val checkedCategories=mutableListOf<String>()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.categories))
            .setMultiChoiceItems(categories, choicesInitial) { dialog, which, isChecked ->
                // mark the checked category on choicesInitial list
                choicesInitial[which]=true
                // add the checked category to checkedCategories list
                checkedCategories += categories[which]
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ -> setWantedAndCheckedCategories(checkedCategories, choicesInitial) }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .show()
    }

    /**
     * sets categories for seach and as preticked if category selection dialog is reopened
     */
    private fun setWantedAndCheckedCategories(checkedCategories:List<String>, tickedCategories:BooleanArray)
    {
        this.initialTickedCategories = tickedCategories
        this.wantedCategories = checkedCategories
    }

    /**
     * checks for calorie params being valid values and enables or disables the search button accordingly
     */
    private fun checkParams():Boolean
    {
        val minimalCalories=binding.caloriesSlider.values[0]
        val maximalCalories=binding.caloriesSlider.values[1]
        val paramsAreValid=maximalCalories!=null && !minimalCalories.isNaN() && !maximalCalories.isNaN() && minimalCalories>=lowestPossibleCalorieScore && maximalCalories<=highestPossibleCalorieScore
        binding.getMealButton.isEnabled=paramsAreValid

        return paramsAreValid
    }

    /**
     * filters all meals for a correct outcome according to user params if given
     */
    private fun startSearch()
    {
        viewModel.allMeals.observe(this.viewLifecycleOwner) { allMeals ->
            val filteredByName: List<Meal> = if(!binding.mealNameInput.text.isNullOrEmpty()) allMeals.filter{ meal -> meal.name.contains(binding.mealNameInput.text.toString(),true)} else allMeals
            val filteredByIsVeggie: List<Meal> = if(binding.veggieCheckbox.isChecked) filteredByName.filter { meal -> meal.isVeggie} else filteredByName
            val filteredByIsVegan: List<Meal> = if(binding.veganCheckbox.isChecked) filteredByIsVeggie.filter { meal -> meal.isVegan} else filteredByIsVeggie
            val filteredByCategories: List<Meal> = if(wantedCategories.isNotEmpty()) filteredByIsVegan.filter { meal -> wantedCategories.contains(meal.category)} else filteredByIsVegan
            val filteredByCalories: List<Meal> = filteredByCategories.filter { meal -> meal.calories>=binding.caloriesSlider.values[0].roundToInt() && meal.calories<=binding.caloriesSlider.values[1].roundToInt()}

            if(filteredByCalories.isNullOrEmpty())
            {
                Toast.makeText(requireContext(), getString(R.string.noMealWithParamsAvailableToast), Toast.LENGTH_SHORT).show()
            }
            else
            {
                val allValidIDs:MutableList<Int> = emptyList<Int>().toMutableList()
                filteredByCalories.forEach { allValidIDs.add(it.ID!!)}
                val action: NavDirections
                action = if(allValidIDs.size==1) SearchFragmentDirections.actionNavigationSearchToMealListFragment(allValidIDs.toIntArray(),getString(R.string.title_singleResult))
                else SearchFragmentDirections.actionNavigationSearchToMealListFragment(allValidIDs.toIntArray(),getString(R.string.title_results,allValidIDs.size))
                findNavController().navigate(action)
            }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}