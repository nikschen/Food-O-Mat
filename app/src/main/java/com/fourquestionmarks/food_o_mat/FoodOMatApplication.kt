package com.fourquestionmarks.food_o_mat

import android.app.Application
import com.fourquestionmarks.food_o_mat.data.FoodOMatDatabase

class FoodOMatApplication : Application()  {

    val database: FoodOMatDatabase by lazy { FoodOMatDatabase.getDatabase(this) }
}