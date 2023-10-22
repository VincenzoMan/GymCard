package com.vp7.gymcard

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.squareup.picasso.Picasso
import com.vp7.gymcard.adpter.CardAdapter
import com.vp7.gymcard.databinding.ActivityExerciseBinding
import com.vp7.gymcard.widget.ExerciseCard
import java.net.URL

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
    }

    private fun initOnClick() {

    }

    private fun initRecycle() {
        binding.recycle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recycle)
        viewModel.exerciseForDay.observe(this) { it ->
            for(day in it) {
                for(exId in day.ids)
                    viewModel.readExercise(exId) {
                        val card = ExerciseCard(this, null)
                        card.setImage(it)
                        exercises.add(it)
                        binding.recycle.adapter = CardAdapter(exercises)
                    }
            }
        }
    }
}