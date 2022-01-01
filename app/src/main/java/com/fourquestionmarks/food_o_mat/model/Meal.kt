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

    override fun toString(): String {
        val veggie= if (isVeggie) "ja" else "nein"
        val vegan= if (isVegan) "ja" else "nein"
        var returnString =""
        returnString += "$name;"
        returnString += "$ID;"
        returnString += "$category;"
        returnString += "${calories.toString().replace(".",",")};"
        returnString += "${carbohydrates.toString().replace(".",",")};"
        returnString += "${proteins.toString().replace(".",",")};"
        returnString += "${fats.toString().replace(".",",")};"
        returnString += "${veggie};"
        returnString += "${vegan};"
        return returnString
    }
}