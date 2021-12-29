package com.fourquestionmarks.food_o_mat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fourquestionmarks.food_o_mat.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@Database(entities = [Meal::class], version = 3, exportSchema = false)
abstract class FoodOMatDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

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
                    .addCallback(FOMDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                return instance
            }
        }
    }

    //callback for prefilling the database with test data
    private class FOMDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabaseWithMeals(database.mealDao())
                }
            }
        }


        // prefilling database functions for each entity
        suspend fun populateDatabaseWithMeals(mealDao: MealDao) {

            var meal = Meal(0, "Testname", "Auflauf", 123.4f, 43.21f, 12.3f, 10.5f, true, false)
            mealDao.insert(meal)
            meal = Meal(1, "Testname2", "Auflauf", 1234.5f, 43.21f, 12.3f, 10.5f, true, true)
            mealDao.insert(meal)
        }
    }
}