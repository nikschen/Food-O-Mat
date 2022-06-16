package com.fourquestionmarks.food_o_mat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity (tableName="ingredients")
data class Ingredient(
    @NotNull
    @PrimaryKey (autoGenerate = true)
    val ID:Int?=0,
    @NotNull
    var name: String,
    @NotNull
    var calories: Int,
    @NotNull
    var carbohydrates: Float,
    @NotNull
    var proteins: Float,
    @NotNull
    var fats: Float,
    var mealID: Int?=-1,
    )

{


//    override fun toString(): String {
//
//        var returnString =""
//        returnString += "$name;"
//        returnString += "$ID;"
//        returnString += "$calories;"
//        returnString += "${carbohydrates.toString().replace(".",",")};"
//        returnString += "${proteins.toString().replace(".",",")};"
//        returnString += "${fats.toString().replace(".",",")};"
//        return returnString
//    }
}