package com.fourquestionmarks.food_o_mat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.MealCardBinding
import com.fourquestionmarks.food_o_mat.model.Meal

class MealListAdapter(private val onMealClicked: (Meal) -> Unit) :
    ListAdapter<Meal, MealListAdapter.MealViewHolder>(DiffCallback) {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
        context=parent.context
        return MealViewHolder(MealCardBinding.inflate(view,parent,false))
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onMealClicked(current)
        }
        holder.bind(current,context)
    }

    class MealViewHolder(private var binding: MealCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal, context: Context) {
            binding.apply {
                name.text=meal.name
                category.text=meal.category
                calories.text=meal.calories.toString()
                carbohydrates.text=meal.carbohydrates.toString()
                proteins.text=meal.proteins.toString()
                fats.text=meal.fats.toString()
                isVeggieCheckbox.isChecked=meal.isVeggie
                isVeganCheckbox.isChecked=meal.isVegan
            }

        }

        private fun isVeggie(meal: Meal): String
        {
            if (meal.isVeggie) return "Ja"
            else return "Nein"
        }
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Meal>() {
            override fun areItemsTheSame(oldMeal: Meal, newMeal: Meal): Boolean {
                return oldMeal === newMeal
            }

            override fun areContentsTheSame(oldMeal: Meal, newMeal: Meal): Boolean {
                return oldMeal.name == newMeal.name
            }
        }
    }
}