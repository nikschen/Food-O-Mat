package com.fourquestionmarks.food_o_mat.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
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

/**
 * [Fragment] to create a new [Meal] entry for the database according to user input
 * requires a name, a category, calories and nutritions of the meal
 * */
class AddOrUpdateMealFragment : Fragment() {
    private val viewModel: MealViewModel by activityViewModels {MealViewModelFactory((activity?.application as FoodOMatApplication).database.mealDao())}
    private val navigationArgs: AddOrUpdateMealFragmentArgs by navArgs()
    private lateinit var allCategories: List<String>
    lateinit var meal: Meal

    private var _binding: FragmentAddOrUpdateMealBinding? = null
    private val binding get() = _binding!!

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { binding.mealImage.setImageURI(uri) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrUpdateMealBinding.inflate(inflater, container, false)

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



            if(param==calories||checkAll)
            {
                when {
                    calories.text.isNullOrEmpty() ->
                    {
                        caloriesLabel.error = getString(R.string.caloriesMissingError)
                        isValid = false
                    }
                    calories.text.toString().replace(',','.').toFloatOrNull() == null ->
                    {
                        caloriesLabel.error = getString(R.string.invalidNumberFormatError)
                        isValid = false
                    }
                    calories.text.toString().replace(',','.').toFloat() > 1500.0f -> caloriesLabel.error = getString(R.string.caloriesVeryHighError)
                }

            }
            else caloriesLabel.error=null

            if(param==carbohydrates||checkAll)
            {
                when {
                    carbohydrates.text.isNullOrEmpty() ->
                    {
                        carbohydratesLabel.error = getString(R.string.carbonhydratesMissingError)
                        isValid = false
                    }
                    carbohydrates.text.toString().replace(',','.').toFloatOrNull() == null ->
                    {
                        carbohydratesLabel.error = getString(R.string.invalidNumberFormatError)
                        isValid = false
                    }
                    else -> carbohydratesLabel.error = null
                }
            }

            if(param==proteins||checkAll) {
                when {
                    proteins.text.isNullOrEmpty() -> {
                        proteinsLabel.error = getString(R.string.proteinsMissingError)
                        isValid = false
                    }
                    proteins.text.toString().replace(',','.').toFloatOrNull() == null -> {
                        proteinsLabel.error = getString(R.string.invalidNumberFormatError)
                        isValid = false
                    }
                    else -> proteinsLabel.error = null
                }
            }


            if(param==fats||checkAll) {
                when {
                    fats.text.isNullOrEmpty() -> {
                        fatsLabel.error = getString(R.string.fatsMissingError)
                        isValid = false
                    }
                    fats.text.toString().replace(',','.').toFloatOrNull() == null -> {
                        fatsLabel.error = getString(R.string.invalidNumberFormatError)
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
     * Inserts the new Meal into database and navigates to detail fragment of the inserted meal
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
                    ID=meal.ID,
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
     *
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


//            mealImage.setOnClickListener {getMealImage()}
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

    private fun getMealImage()
    {
        selectImageFromGalleryResult.launch("image/*")
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