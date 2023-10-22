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
import com.vp7.gymcard.adpter.ResultData
import java.io.IOException

class ExerciseViewModel(context: Context) {
    private val db : FirebaseFirestore = Firebase.firestore
    private var userId: Int = 0
    private var exercises: MutableList<ExerciseForDay> = mutableListOf()
    val exerciseForDay: MutableLiveData<ExerciseForDay> = MutableLiveData()
    val daysForWeek: MutableLiveData<Int> = MutableLiveData()
    fun readUserData(userId: Int) {
        db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener { snapshot ->
                for (document in snapshot.result.documents) {
                    val userId = document.getLong("userId")?.toInt() ?: 0
                    val days = document.getLong("dayForWeek")?.toInt() ?: 0
                    readExercises(1)
                    Log.d(TAG,"userId $userId")
                    daysForWeek.postValue(days)
                }
            }
    }

    fun readExercises(day: Long) {
        exercises.clear()
        db.collection("user_form")
            .whereEqualTo("userId", userId)
            .whereEqualTo("day", day)
            .get()
            .addOnCompleteListener { snapshot ->
                if(snapshot.result.documents.isEmpty()) exerciseForDay.postValue(null)
                for (document in snapshot.result.documents) {
                    val ids = document.get("exIds") as List<Int>
                    exerciseForDay.postValue(ExerciseForDay(day.toInt(), ids))
                }
            }
    }
    fun readResult(ids: List<Int>, day: Long, onResult: (MutableMap<Int,MutableList<ResultExerciseData>>) -> Unit) {
        exercises.clear()
        val results: MutableMap<Int,MutableList<ResultExerciseData>> = mutableMapOf()
        db.collection("result")
            .whereEqualTo("userId", userId)
            .whereEqualTo("dayNum", day)
            .whereIn("exId", ids)
            .whereEqualTo("weekNum", 1)
            .get()
            .addOnCompleteListener { snapshot ->
                if(snapshot.result.documents.isEmpty()) {
                    onResult.invoke(results)
                    return@addOnCompleteListener
                }
                for (document in snapshot.result.documents) {
                    val result = document.getString("result") ?: ""
                    val numSeries = document.getLong("seriesNum")?.toInt() ?: 0
                    val exId = document.getLong("exId")?.toInt() ?: 0
                    results.getOrPut(exId) { mutableListOf() }.add(ResultExerciseData(numSeries,result))
                }
                onResult.invoke(results)
            }
    }

    fun readExercise(day: Int, exId: List<Int>, callback: (MutableList<ExerciseInfo>) -> Unit) {
        val exercises : MutableList<ExerciseInfo> = mutableListOf()
        var result: MutableList<ResultExerciseData> = mutableListOf()
        db.collection("gym")
            .whereIn("exId", exId)
            .get()
            .addOnCompleteListener { snapshot ->
                val resultCount = snapshot.result.documents.size
                var resultCounter = 0
                for (document in snapshot.result.documents) {
                    val imageUrl = document.getString("imageId") ?: ""
                    val exerciseId = document.getLong("exId")?.toInt() ?: 0
                    val name = document.getString("name") ?: ""
                    val numRep = document.get("repNum") as List<*>?
                    readResult(exId,day.toLong()) {
                        resultCounter++
                        result = it[exerciseId] ?: mutableListOf()
                        exercises.add(ExerciseInfo(exerciseId, name, imageUrl, numRep, day, result))
                        if (resultCounter == resultCount) {
                            callback.invoke(exercises)
                        }
                    }
                }
            }
    }
    fun saveResult(result: ResultData) {
        result.kg.forEachIndexed { index, s ->
            val docData = hashMapOf(
                "dayNum" to result.day,
                "exId" to result.exId,
                "result" to s,
                "seriesNum" to index+1,
                "userId" to userId,
                "weekNum" to 1,
            )
            db.collection("result")
                .add(docData)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }
}

class ExerciseForDay(val day: Int, val ids: List<Int>)
class ResultExerciseData(val numSeries: Int, val result: String)
class ExerciseInfo(val id: Int, val name: String, val image: String, val numRepDefault: List<*>?, val day: Int, val result: List<ResultExerciseData>)