package com.wak.submission2fundamental.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wak.submission2fundamental.data.UserFollow
import com.wak.submission2fundamental.R
import de.hdodenhof.circleimageview.CircleImageView

class FollowUserAdapter(private val listFollow: ArrayList<UserFollow>) : RecyclerView.Adapter<FollowUserAdapter.ListViewHolder>() {

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val userData = listFollow[position]
        Glide.with(holder.itemView.context)
            .load(userData.avatar)
            .into(holder.imgAvatar)
        holder.txtUsername.text = userData.username
    }
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAvatar: CircleImageView = itemView.findViewById(R.id.userfollow_img)
        var txtUsername: TextView = itemView.findViewById(R.id.userfollow_username)
    }
    override fun getItemCount(): Int = listFollow.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_userfollow, viewGroup, false)
        return ListViewHolder(view)
    }
}