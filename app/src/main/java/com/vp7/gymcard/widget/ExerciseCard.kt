package com.vp7.gymcard.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import com.vp7.gymcard.ExerciseInfo
import com.vp7.gymcard.R
import com.vp7.gymcard.databinding.CardExerciseBinding

class ExerciseCard(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

private lateinit var binding: CardExerciseBinding

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.card_exercise,
            this,
            true
        )
    }

    fun setImage(exerciseInfo: ExerciseInfo) {


        binding.nameExercise.text = exerciseInfo.name
        binding.repExercise.text = exerciseInfo.numRepDefault.toString()
    }

}