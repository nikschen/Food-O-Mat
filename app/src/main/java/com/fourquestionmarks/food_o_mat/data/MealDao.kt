package com.fourquestionmarks.food_o_mat.data

import androidx.room.*
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(meal: Meal)

    @Delete
    fun delete(meal: Meal)

    @Update
    fun update(meal: Meal)

    @Query("SELECT * FROM meals where id= :ID")
    fun getMealByID(ID:Int):Flow<Meal>

    @Query ("SELECT * FROM meals")
    fun getMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id=(SELECT MAX(id) from meals) order by id DESC ")
    fun getLastInsertedMeal():Flow<Meal>
}