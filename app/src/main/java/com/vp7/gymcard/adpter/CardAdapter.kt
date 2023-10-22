package com.vp7.gymcard.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vp7.gymcard.ExerciseInfo
import com.vp7.gymcard.R

class CardAdapter(private var data: MutableList<ExerciseInfo>, private val onSave: (ResultData) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_exercise, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = data[position]
        val rep1Value = holder.itemView.findViewById<TextView>(R.id.rep_1_value)
        val rep2Value = holder.itemView.findViewById<TextView>(R.id.rep_2_value)
        val rep3Value = holder.itemView.findViewById<TextView>(R.id.rep_3_value)
        Picasso.get()
            .load(item.image)
            .into(holder.itemView.findViewById<ImageView>(R.id.imageView))
        holder.itemView.findViewById<TextView>(R.id.name_exercise).text = item.name
        item.numRepDefault?.let {
            holder.itemView.findViewById<TextView>(R.id.rep_1).text = item.numRepDefault[0].toString()
            holder.itemView.findViewById<TextView>(R.id.rep_2).text = item.numRepDefault[1].toString()
            holder.itemView.findViewById<TextView>(R.id.rep_3).text = item.numRepDefault[2].toString()
            if( item.result.isNotEmpty()) {
                item.result.forEach {
                    when(it.numSeries) {
                        1 -> rep1Value.text = it.result
                        2 -> rep2Value.text = it.result
                        3 -> rep3Value.text = it.result
                    }
                }
            }
            holder.itemView.findViewById<TextView>(R.id.save_button).setOnClickListener {
                val rep1 = rep1Value.text.toString()
                val rep2 = rep2Value.text.toString()
                val rep3 = rep3Value.text.toString()
                onSave.invoke(ResultData(listOf(rep1,rep2,rep3), item.id, item.day))
            }
        }

    }

    fun clear() {
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemCount() = data.size
}
class ResultData(val kg: List<String>, val exId: Int, val day: Int)