package com.fourquestionmarks.food_o_mat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity (tableName="meals")
data class Meal(
    @NotNull
    @PrimaryKey (autoGenerate = true)
    val ID:Int?=0,
    @NotNull
    var name: String,
    @NotNull
    var category: String,
    @NotNull
    var calories: Float,
    @NotNull
    var carbohydrates: Float,
    @NotNull
    var proteins: Float,
    @NotNull
    var fats: Float,
    @NotNull
    var isVeggie: Boolean,
    @NotNull
    var isVegan: Boolean,
    )
{
}