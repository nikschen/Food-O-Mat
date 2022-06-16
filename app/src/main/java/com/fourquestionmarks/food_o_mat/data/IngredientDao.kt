package com.fourquestionmarks.food_o_mat.data

import androidx.room.*
import com.fourquestionmarks.food_o_mat.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)

    @Update
    suspend fun update(ingredient: Ingredient)

    @Query("SELECT * FROM ingredients where id= :ID")
    fun getIngredientByID(ID:Int):Flow<Ingredient>

    @Query("SELECT * FROM ingredients where mealID= :mealID")
    fun getIngredientsByMealID(mealID:Int):Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredients where name= :name")
    fun getIngredientByName(name:String):Ingredient

    @Query ("SELECT * FROM ingredients")
    fun getIngredients(): Flow<List<Ingredient>>

    @Query ("SELECT * FROM ingredients")
    fun getIngredientsAsList(): List<Ingredient>

    @Query("SELECT * FROM ingredients WHERE id=(SELECT MAX(id) from ingredients) order by id DESC ")
    fun getLastInsertedIngredient():Flow<Ingredient>

}