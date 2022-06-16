package com.fourquestionmarks.food_o_mat.ui

import androidx.lifecycle.*
import com.fourquestionmarks.food_o_mat.data.IngredientDao
import com.fourquestionmarks.food_o_mat.model.Ingredient
import kotlinx.coroutines.launch

class IngredientViewModel(private val ingredientDao: IngredientDao) : ViewModel() {

    // Cache all ingredients from the database using LiveData.
    val allIngredients: LiveData<List<Ingredient>> = ingredientDao.getIngredients().asLiveData()

    /**
     * Launching a new coroutine to update a [Ingredient] in a non-blocking way
     */
    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientDao.update(ingredient)
        }
    }

    /**
     * Launching a new coroutine to insert a [Ingredient] in a non-blocking way
     */
    fun insertIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientDao.insert(ingredient)
        }
    }

    /**
     * Launching a new coroutine to delete a [Ingredient] in a non-blocking way
     */
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientDao.delete(ingredient)
        }
    }

    /**
     * Retrieve all [Ingredient]s from the database as a List.
     */
    fun getIngredientsAsList(): List<Ingredient> {
        return ingredientDao.getIngredientsAsList()
    }

    /**
     * Retrieve a specific [Ingredient] from the database.
     */
    fun getIngredientById(id: Int): LiveData<Ingredient> {
        return ingredientDao.getIngredientByID(id).asLiveData()
    }

    /**
     * Retrieve a specific [Ingredient] from the database.
     */
    fun getIngredientsByMealId(mealID: Int): LiveData<List<Ingredient>> {
        return ingredientDao.getIngredientsByMealID(mealID).asLiveData()
    }

    /**
     * Retrieve a specific [Ingredient] from the database by name if available.
     */
    fun getIngredientByName(name: String): Ingredient {
        return ingredientDao.getIngredientByName(name)
    }

    /**
     * Get ID of last inserted ingredient
     */
    fun getLastInsertedIngredient():LiveData<Ingredient>{
        return ingredientDao.getLastInsertedIngredient().asLiveData()
    }

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class IngredientViewModelFactory(private val ingredientDao: IngredientDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngredientViewModel(ingredientDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}