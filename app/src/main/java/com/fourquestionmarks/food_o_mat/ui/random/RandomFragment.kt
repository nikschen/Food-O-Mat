package com.fourquestionmarks.food_o_mat.ui.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fourquestionmarks.food_o_mat.databinding.FragmentRandomBinding

class RandomFragment : Fragment() {

//    private lateinit var randomViewModel: RandomViewModel
    private var _binding: FragmentRandomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        randomViewModel =
//            ViewModelProvider(this).get(RandomViewModel::class.java)

        _binding = FragmentRandomBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        randomViewModel.text.observe(viewLifecycleOwner, Observer {
//        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}