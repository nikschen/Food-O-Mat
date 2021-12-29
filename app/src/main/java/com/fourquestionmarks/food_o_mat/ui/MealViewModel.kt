package com.fourquestionmarks.food_o_mat.ui

import androidx.lifecycle.*
import com.fourquestionmarks.food_o_mat.data.MealDao
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.launch

class MealViewModel(private val mealDao: MealDao) : ViewModel() {

    // Cache all meals from the database using LiveData.
    val allMeals: LiveData<List<Meal>> = mealDao.getMeals().asLiveData()

    /**
     * Launching a new coroutine to update a [Meal] in a non-blocking way
     */
    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.update(meal)
        }
    }

    /**
     * Launching a new coroutine to insert a [Meal] in a non-blocking way
     */
    fun insertMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.insert(meal)
        }
    }

    /**
     * Launching a new coroutine to delete a [Meal] in a non-blocking way
     */
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealDao.delete(meal)
        }
    }

    /**
     * Retrieve a specific [Meal] from the database.
     */
    fun getMealById(id: Int): LiveData<Meal> {
        return mealDao.getMealByID(id).asLiveData()
    }

    /**
     * Get ID of last inserted meal
     */
    fun getLastInsertedMeal():LiveData<Meal>{
        return mealDao.getLastInsertedMeal().asLiveData()
    }

    /**
     * Get all distinct categories stored in database
     */
    fun getAllCategories():List<String>{
        return mealDao.getAllCategories()
    }

    /**
     * Get lowest calorie score of all meals stored in database
     */
    fun getLowestCalorieScore():Float{
        return mealDao.getLowestCalorieScore()
    }

    /**
     * Get highest calorie score of all meals stored in database
     */
    fun getHighestCalorieScore():Float{
        return mealDao.getHighestCalorieScore()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isValid(mealName: String, mealPrice: String, mealCount: String): Boolean {
        //TODO: sinnvolle Prüfung
//        if (mealName.isBlank() || mealPrice.isBlank() || mealCount.isBlank()) {
//            return false
//        }
        return true
    }



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