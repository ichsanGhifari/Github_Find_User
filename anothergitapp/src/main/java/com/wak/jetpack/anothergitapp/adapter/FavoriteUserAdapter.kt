package com.wak.submission2fundamental.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wak.jetpack.anothergitapp.data.UserFavorite
import com.wak.jetpack.anothergitapp.databinding.ListItemFavoriteBinding


class FavoriteUserAdapter : RecyclerView.Adapter<FavoriteUserAdapter.FavoriteViewHolder>() {
    var userFavorite = ArrayList<UserFavorite>()
        set(userFavorite){
            if(userFavorite.size>0){
                this.userFavorite.clear()
            }
            this.userFavorite.addAll(userFavorite)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {
        val binding =
            ListItemFavoriteBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return FavoriteViewHolder(binding)
    }

    override fun getItemCount(): Int = userFavorite.size

    override fun onBindViewHolder(holder: FavoriteUserAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(userFavorite[position])
    }

    inner class FavoriteViewHolder(private val binding: ListItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userFavorite: UserFavorite) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(userFavorite.avatar)
                    .into(userfollowImg)
                userfollowUsername.text =userFavorite.username
                itemView.setOnClickListener {

                }
            }
        }

    }
}