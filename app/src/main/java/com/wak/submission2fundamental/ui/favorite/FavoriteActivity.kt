package com.wak.submission2fundamental.ui.favorite

import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.adapter.UserFavoriteAdapter
import com.wak.submission2fundamental.data.UserFavorite
import com.wak.submission2fundamental.data.database.DatabaseContract.UserFavColumns.Companion.CONTENT_URI
import com.wak.submission2fundamental.data.database.MappingHelper
import com.wak.submission2fundamental.data.database.UserFavoriteHelper
import com.wak.submission2fundamental.databinding.ActivityFavoriteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    private lateinit var favoriteBinding: ActivityFavoriteBinding
    private lateinit var favoriteAdapter: UserFavoriteAdapter
    companion object{
        const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favoriteBinding.root)
        supportActionBar?.title = resources.getString(R.string.favorite_user) //ganti nama action bar


        //konfigurasi recycler view
        recyclerViewConfig()
        //get data recycler view pada oncreate
        showToRecyclerView()

        //handler untuk load data sqlite
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                loadAsyncUser()
            }
        }
        //content resolver
        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if(savedInstanceState == null){
            loadAsyncUser()
        }
        else{
            val list = savedInstanceState.getParcelableArrayList<UserFavorite>(EXTRA_STATE)
            if(list != null){
                favoriteAdapter.userFavorite = list
            }
        }

    }

    //load asyncuser untuk mendapatkan data dari Sqlite
    private fun loadAsyncUser() {
        GlobalScope.launch(Dispatchers.Main) {
            favoriteBinding.progressBar.visibility = View.VISIBLE
            val favoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredUser = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorite = deferredUser.await()
            favoriteBinding.progressBar.visibility = View.INVISIBLE
            if (favorite.size > 0) {
                favoriteBinding.rvListFavorite.visibility = VISIBLE
                favoriteBinding.rvListFavorite.isEnabled = true
                favoriteBinding.tvStateFavorite.visibility = INVISIBLE
                favoriteAdapter.userFavorite = favorite
            } else {
                favoriteBinding.rvListFavorite.visibility = INVISIBLE
                favoriteBinding.rvListFavorite.isEnabled = false
                favoriteAdapter.userFavorite = ArrayList()
                favoriteBinding.tvStateFavorite.visibility = VISIBLE
                Snackbar.make(
                    favoriteBinding.rvListFavorite,
                    getString(R.string.emptyMessage),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun recyclerViewConfig() {
        //Konfigurasi Recycler View
        favoriteBinding.rvListFavorite.layoutManager =
            LinearLayoutManager(this)
        favoriteBinding.rvListFavorite.setHasFixedSize(true)
        favoriteBinding.rvListFavorite.addItemDecoration(
            DividerItemDecoration(
                favoriteBinding.rvListFavorite.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }


    private fun showToRecyclerView() {
        favoriteAdapter = UserFavoriteAdapter()
        favoriteBinding.rvListFavorite.adapter = favoriteAdapter

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, favoriteAdapter.userFavorite)
    }

    override fun onResume() {
        super.onResume()
        loadAsyncUser()
    }
}