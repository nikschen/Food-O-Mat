package com.fourquestionmarks.food_o_mat

import android.app.Application
import com.fourquestionmarks.food_o_mat.data.FoodOMatDatabase
import com.fourquestionmarks.food_o_mat.data.KeyValueStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FoodOMatApplication : Application()  {

    val applicationScope= CoroutineScope(SupervisorJob())
    val database: FoodOMatDatabase by lazy { FoodOMatDatabase.getDatabase(this, applicationScope) }
    val settings: KeyValueStore= KeyValueStore(this)


}