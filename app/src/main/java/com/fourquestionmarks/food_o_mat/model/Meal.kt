package com.fourquestionmarks.food_o_mat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName="meals")
data class Meal(
    @PrimaryKey (autoGenerate = true)
    val ID:Int?=0,
    var name: String,
    var category: String,
    var calories: Float,
    var carbohydrates: Float,
    var proteins: Float,
    var fats: Float,
    var isVeggie: Boolean)
{
}