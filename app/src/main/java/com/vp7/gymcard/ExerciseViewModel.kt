package com.vp7.gymcard

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException

class ExerciseViewModel(context: Context) {
    private val db : FirebaseFirestore = Firebase.firestore

    private var exercises: MutableList<ExerciseForDay> = mutableListOf()
    val exerciseForDay: MutableLiveData<MutableList<ExerciseForDay>> = MutableLiveData()
    fun readUserData(userId: Int) {

        db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener { snapshot ->
                for (document in snapshot.result.documents) {
                    val campo1 = document.getLong("userId")?.toInt() ?: 0
                    val campo2 = document.getLong("weekNum")?.toInt() ?: 0

                    readExercises(campo1)
                    Log.d(TAG,"userId $campo1")
                }
            }
    }

    private fun readExercises(userId: Int) {
        db.collection("user_form")
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener { snapshot ->
                for (document in snapshot.result.documents) {
                    val ids = document.get("exIds") as List<*>
                    val day = document.getLong("day") ?: 0
                    exercises.add(ExerciseForDay(day,ids))
                }
                exerciseForDay.postValue(exercises)
            }
    }

    fun readExercise(exId: Any?, callback: (ExerciseInfo) -> Unit) {
        db.collection("gym")
            .whereEqualTo("exId", exId)
            .get()
            .addOnCompleteListener { snapshot ->
                for (document in snapshot.result.documents) {
                    val imageUrl = document.getString("imageId") ?: ""
                    val name = document.getString("name") ?: ""
                    val numRep = document.get("repNum") as List<*>?
                    callback.invoke(ExerciseInfo(name, imageUrl, numRep))
                }
            }
    }
}

class ExerciseForDay(val day: Long, val ids: List<*>)
class ExerciseInfo(val name: String, val image: String, val numRepDefault: List<*>?)