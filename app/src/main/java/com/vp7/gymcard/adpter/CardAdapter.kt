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

class CardAdapter(private val data: List<ExerciseInfo>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_exercise, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Personalizza il contenuto della card
        val item = data[position]
        Picasso.get()
            .load(item.image)
            .into(holder.itemView.findViewById<ImageView>(R.id.imageView))
         holder.itemView.findViewById<TextView>(R.id.name_exercise).text = item.name
        holder.itemView.findViewById<TextView>(R.id.rep_exercise).text = item.numRepDefault.toString()
    }



    override fun getItemCount() = data.size
}