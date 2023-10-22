package com.vp7.gymcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.firebase.FirebaseApp
import com.vp7.gymcard.adpter.CardAdapter
import com.vp7.gymcard.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var viewModel: ExerciseViewModel
    private var exercises : MutableList<ExerciseInfo> = mutableListOf()
    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseApp.initializeApp(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise)
        viewModel = ExerciseViewModel(this)
        viewModel.readUserData(0)
        initRecycle()
        initOnClick()
        initObservable()
    }

    private fun initObservable() {
        viewModel.exerciseForDay.observe(this) { exercises ->
            binding.recycle.adapter = null
            if(exercises != null)
                viewModel.readExercise(exercises.day, exercises.ids) {
                    binding.recycle.adapter = CardAdapter(it) {
                        viewModel.saveResult(it)
                    }
                }
            }
        viewModel.daysForWeek.observe(this) {
            if(it == 2) binding.day3.visibility = View.GONE
            if(it == 3) binding.day3.visibility = View.VISIBLE
        }
    }

    private fun initOnClick() {
        binding.day1.setOnClickListener {
            viewModel.readExercises(1)
            binding.day.text = "day 1"
        }
        binding.day2.setOnClickListener {
            viewModel.readExercises(2)
            binding.day.text = "day 2"
        }
        binding.day3.setOnClickListener {
            viewModel.readExercises(3)
            binding.day.text = "day 3"
        }
    }

    private fun initRecycle() {
        binding.recycle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recycle)

    }
}