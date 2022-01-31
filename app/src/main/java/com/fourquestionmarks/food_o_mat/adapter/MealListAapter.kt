package com.fourquestionmarks.food_o_mat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourquestionmarks.food_o_mat.R
import com.fourquestionmarks.food_o_mat.databinding.MealCardShortBinding
import com.fourquestionmarks.food_o_mat.model.Meal

class MealListAdapter(private val onMealClicked: (Meal) -> Unit) :
    ListAdapter<Meal, MealListAdapter.MealViewHolder>(DiffCallback) {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
        context=parent.context
        return MealViewHolder(MealCardShortBinding.inflate(view,parent,false))
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onMealClicked(current)
        }
        holder.bind(current,context)
    }

    class MealViewHolder(private var binding: MealCardShortBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal, context: Context) {
            binding.apply {
                name.text=meal.name
                mealCategory.text=meal.category
                mealCalories.text=context.getString(R.string.caloriesWithUnit,meal.calories)
                if(meal.isVeggie)
                {
                    veggieLabelEmpty.visibility= View.INVISIBLE
                    veggieLabel.visibility= View.VISIBLE
                }
                else
                {
                    veggieLabelEmpty.visibility= View.VISIBLE
                    veggieLabel.visibility= View.INVISIBLE
                }
                if(meal.isVegan)
                {
                    veganLabelEmpty.visibility= View.INVISIBLE
                    veganLabel.visibility= View.VISIBLE
                }
                else
                {
                    veganLabelEmpty.visibility= View.VISIBLE
                    veganLabel.visibility= View.INVISIBLE
                }
            }

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