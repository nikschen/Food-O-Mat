package com.fourquestionmarks.food_o_mat.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fourquestionmarks.food_o_mat.model.Ingredient
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId


@Database(entities = [Meal::class, Ingredient::class], version = 11, exportSchema = true, autoMigrations = [AutoMigration(from=9, to=10)])
abstract class FoodOMatDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        @Volatile
        private var INSTANCE: FoodOMatDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FoodOMatDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodOMatDatabase::class.java,
                    "meal_db"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                return instance
            }
        }
    }

}