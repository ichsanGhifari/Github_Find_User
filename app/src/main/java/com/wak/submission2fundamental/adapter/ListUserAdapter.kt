package com.wak.submission2fundamental.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.data.User
import com.wak.submission2fundamental.databinding.ListUserBinding
import com.wak.submission2fundamental.ui.detailuser.DetailUserActivity

class ListUserAdapter(private val listUser: ArrayList<User>) :
    RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ListUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
        //getDetailUser()

    }

    override fun getItemCount(): Int = listUser.size
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])

    }

    inner class ListViewHolder(private val binding: ListUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(userData: User) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(userData.avatar)
                    .into(gituserImg)
                gituserUsername.text = "@${userData.username}"
                //gituserUrl.text = userData.html
                gituserName.text = userData.name
                gituserRepo.text = itemView.context.getString(R.string.detail_repocomnull, userData.repo)
                gituserFollow.text =
                    itemView.context.getString(R.string.follow, userData.following, userData.followers)
                itemView.setOnClickListener {
                    val intentDetailUser = Intent(itemView.context, DetailUserActivity::class.java)
                    intentDetailUser.putExtra(DetailUserActivity.GITUSER_DATA, userData)
                    itemView.context.startActivity(intentDetailUser)
                }
            }
        }

    }


}
