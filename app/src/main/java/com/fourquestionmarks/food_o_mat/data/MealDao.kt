package com.fourquestionmarks.food_o_mat.data

import androidx.room.*
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Update
    suspend fun update(meal: Meal)

    @Query("SELECT * FROM meals where id= :ID")
    fun getMealByID(ID:Int):Flow<Meal>

    @Query ("SELECT * FROM meals")
    fun getMeals(): Flow<List<Meal>>

    @Query ("SELECT * FROM meals")
    fun getMealsAsList(): List<Meal>

    @Query("SELECT * FROM meals WHERE id=(SELECT MAX(id) from meals) order by id DESC ")
    fun getLastInsertedMeal():Flow<Meal>

    @Query("SELECT MIN(calories) FROM meals")
    fun getLowestCalorieScore(): Float

    @Query("SELECT MAX(calories) FROM meals")
    fun getHighestCalorieScore(): Float

    @Query("SELECT DISTINCT category from meals")
    fun getAllCategories():List<String>

    @Query("SELECT COUNT(ID) from meals")
    fun getMealCount():Int
}