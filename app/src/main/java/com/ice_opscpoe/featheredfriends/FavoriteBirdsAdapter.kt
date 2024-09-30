package com.ice_opscpoe.featheredfriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteBirdsAdapter(private var birds: List<FavoriteBird>) : RecyclerView.Adapter<FavoriteBirdsAdapter.BirdViewHolder>() {

    class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdName: TextView = itemView.findViewById(R.id.birdNameTextView)
        val birdImage: ImageView = itemView.findViewById(R.id.birdImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_bird, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]
        holder.birdName.text = bird.name
        Glide.with(holder.itemView.context).load(bird.imageUri).into(holder.birdImage)
    }

    override fun getItemCount(): Int = birds.size

    fun updateBirds(newBirds: List<FavoriteBird>) {
        birds = newBirds
        notifyDataSetChanged()
    }
}
//Reference List
