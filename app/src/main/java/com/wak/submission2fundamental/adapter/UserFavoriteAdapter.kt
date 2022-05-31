package com.wak.submission2fundamental.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wak.submission2fundamental.data.UserFavorite
import com.wak.submission2fundamental.databinding.ListFavoriteBinding
import com.wak.submission2fundamental.ui.detailuser.DetailUserActivity

class UserFavoriteAdapter : RecyclerView.Adapter<UserFavoriteAdapter.FavoriteViewHolder>() {
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
            ListFavoriteBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return FavoriteViewHolder(binding)
    }

    override fun getItemCount(): Int = userFavorite.size

    override fun onBindViewHolder(holder: UserFavoriteAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(userFavorite[position])
    }

    inner class FavoriteViewHolder(private val binding: ListFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userFavorite: UserFavorite) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(userFavorite.avatar)
                    .into(userfollowImg)
                userfollowUsername.text =userFavorite.username
                itemView.setOnClickListener {
                    val intentDetailUser = Intent(itemView.context, DetailUserActivity::class.java)
                    intentDetailUser.putExtra(DetailUserActivity.GITUSER_FAV, userFavorite)
                    itemView.context.startActivity(intentDetailUser)
                }
            }
        }

    }
}