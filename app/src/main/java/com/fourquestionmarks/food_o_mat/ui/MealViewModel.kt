package com.fourquestionmarks.food_o_mat.ui

import androidx.lifecycle.*
import com.fourquestionmarks.food_o_mat.data.MealDao
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.launch

class MealViewModel(private val mealDao: MealDao) : ViewModel() {

    // Cache all meals from the database using LiveData.
    val allMeals: LiveData<List<Meal>> = mealDao.getMeals().asLiveData()

    /**
     * Updates an existing _root_ide_package_.com.fourquestionmarks.food_o_mat.model.Meal in the database.
     */
    fun updateMeal(
        ID: Int,
        name: String,
        category: String,
        calories: Float,
        carbohydrates: Float,
        proteins: Float,
        fats: Float,
        isVeggie: Boolean
    ) {
        val updatedMeal = Meal(
            ID = ID,
            name = name,
            category = category,
            calories = calories,
            carbohydrates = carbohydrates,
            proteins = proteins,
            fats = fats,
            isVeggie = isVeggie
        )
        updateMeal(updatedMeal)
    }


    /**
     * Launching a new coroutine to update an meal_card in a non-blocking way
     */
    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.update(meal)
        }
    }


    /**
     * Inserts the new Meal into database.
     */
    fun addNewMeal(
        name: String,
        category: String,
        calories: Float,
        carbohydrates: Float,
        proteins: Float,
        fats: Float,
        isVeggie: Boolean
    ) {
        val newMeal = Meal(
            name = name,
            category = category,
            calories = calories,
            carbohydrates = carbohydrates,
            proteins = proteins,
            fats = fats,
            isVeggie = isVeggie
        )
        updateMeal(newMeal)
    }

    /**
     * Launching a new coroutine to insert an meal_card in a non-blocking way
     */
    fun insertMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.insert(meal)
        }
    }

    /**
     * Launching a new coroutine to delete an meal_card in a non-blocking way
     */
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.delete(meal)
        }
    }

    /**
     * Retrieve an meal_card from the repository.
     */
    fun getMealById(id: Int): LiveData<Meal> {
        return mealDao.getMealByID(id).asLiveData()
    }

    fun getLastInsertedMeal():LiveData<Meal>{
        return mealDao.getLastInsertedMeal().asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isValid(mealName: String, mealPrice: String, mealCount: String): Boolean {
//        if (mealName.isBlank() || mealPrice.isBlank() || mealCount.isBlank()) {
//            return false
//        }
        return true
    }


    /**
     * Factory class to instantiate the [ViewModel] instance.
     */
    class MealViewModelFactory(private val mealDao: MealDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MealViewModel(mealDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}