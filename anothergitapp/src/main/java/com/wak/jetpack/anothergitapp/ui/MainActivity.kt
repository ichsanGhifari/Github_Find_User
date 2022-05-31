package com.wak.jetpack.anothergitapp.ui

import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wak.jetpack.anothergitapp.R
import com.wak.jetpack.anothergitapp.data.UserFavorite
import com.wak.jetpack.anothergitapp.data.UserFavoriteHelper
import com.wak.jetpack.anothergitapp.data.database.DatabaseContract.UserFavColumns.Companion.CONTENT_URI
import com.wak.jetpack.anothergitapp.data.database.MappingHelper
import com.wak.jetpack.anothergitapp.databinding.ActivityMainBinding
import com.wak.submission2fundamental.adapter.FavoriteUserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var favoriteAdapter: FavoriteUserAdapter
    companion object{
        const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        supportActionBar?.title = "Favorite Users"
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
            activityMainBinding.progressBar.visibility = View.VISIBLE
            val favoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredUser = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorite = deferredUser.await()
            activityMainBinding.progressBar.visibility = View.INVISIBLE
            if (favorite.size > 0) {
                activityMainBinding.rvListFavorite.visibility = View.VISIBLE
                activityMainBinding.rvListFavorite.isEnabled = true
                activityMainBinding.tvStateFavorite.visibility = View.INVISIBLE
                favoriteAdapter.userFavorite = favorite
            } else {
                activityMainBinding.rvListFavorite.visibility = View.INVISIBLE
                activityMainBinding.rvListFavorite.isEnabled = false
                favoriteAdapter.userFavorite = ArrayList()
                activityMainBinding.tvStateFavorite.visibility = View.VISIBLE
                Snackbar.make(
                    activityMainBinding.rvListFavorite,
                    getString(R.string.emptyMessage),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun recyclerViewConfig() {
        //Konfigurasi Recycler View
        activityMainBinding.rvListFavorite.layoutManager =
            LinearLayoutManager(this)
        activityMainBinding.rvListFavorite.setHasFixedSize(true)
        activityMainBinding.rvListFavorite.addItemDecoration(
            DividerItemDecoration(
                activityMainBinding.rvListFavorite.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }


    private fun showToRecyclerView() {
        favoriteAdapter = FavoriteUserAdapter()
        activityMainBinding.rvListFavorite.adapter = favoriteAdapter

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