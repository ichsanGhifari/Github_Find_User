package com.wak.submission2fundamental.ui.main


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.wak.submission2fundamental.adapter.ListUserAdapter
import com.wak.submission2fundamental.data.MainData
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.data.User
import com.wak.submission2fundamental.databinding.ActivityMainBinding
import com.wak.submission2fundamental.ui.settings.SettingsActivity
import com.wak.submission2fundamental.ui.favorite.FavoriteActivity
import com.wak.submission2fundamental.ui.settings.MyPreferences
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val searchList = ArrayList<User>()
    private val client = AsyncHttpClient()
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_favorite-> startActivity(Intent(this, FavoriteActivity::class.java))
        }
        /*if (item.itemId == R.id.menu_settings) {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }*/
        return super.onOptionsItemSelected(item)
    }

    private fun recyclerViewConfig() {
        //Konfigurasi Recycler View
        binding.rvListSearch.layoutManager = LinearLayoutManager(binding.rvListSearch.context)
        binding.rvListSearch.setHasFixedSize(true)
        binding.rvListSearch.addItemDecoration(
            DividerItemDecoration(
                binding.rvListSearch.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun showToRecyclerView() {
        //Kalau udah Success di Http tampilin RecyclerList
        binding.rvListSearch.layoutManager = LinearLayoutManager(this)
        val gituserAdapter = ListUserAdapter(searchList)
        binding.rvListSearch.adapter = gituserAdapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionbar = supportActionBar
        actionbar!!.title = "Github User Search"
        recyclerViewConfig()
        searchUser()
    }


    private fun searchUser() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    return true
                } else {
                    searchList.clear()
                    getGitUserNameSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getGitUserNameSearch(username: String) {
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token ${MainData.gitToken}")
        val url = "https://api.github.com/search/users?q=$username"
        binding.progressBar.visibility = View.VISIBLE
        binding.rvListSearch.visibility = View.INVISIBLE
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?
            ) {
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val jsonGitObject = JSONObject(result)
                    val countItem = jsonGitObject.getInt("total_count")
                    if (countItem != 0) {
                        val userObjectArray = jsonGitObject.getJSONArray("items")
                        for (i in 0 until userObjectArray.length()) {
                            val userName = userObjectArray.getJSONObject(i).getString("login")
                            getGitUserNameSearchDetail(userName)
                            //searchList.add(User(userName))
                        }
                        //binding.progressBar.visibility = View.INVISIBLE
                        //showToRecyclerView()

                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.rvListSearch.visibility = View.INVISIBLE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.errorMessage.text = resources.getString(R.string.error_notfound)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val err = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                binding.progressBar.visibility = View.INVISIBLE
                binding.rvListSearch.visibility = View.INVISIBLE
                binding.errorLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.errorMessage.text = resources.getString(R.string.error_occured)
                Toast.makeText(this@MainActivity, err, Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun getGitUserNameSearchDetail(userName: String) {
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token ${MainData.gitToken}")
        val urlUser = "https://api.github.com/users/$userName"
        binding.progressBar.visibility = View.VISIBLE
        client.get(urlUser, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val username: String? = jsonObject.getString("login")
                    val name: String? = jsonObject.getString("name")
                    val avatar: String? = jsonObject.getString("avatar_url")
                    val following: Int = jsonObject.getInt("following")
                    val followers: Int = jsonObject.getInt("followers")
                    val company: String? = jsonObject.getString("company")
                    val repo: Int = jsonObject.getInt("public_repos")
                    val country: String? = jsonObject.getString("location")
                    searchList.add(
                        User(
                            username,
                            avatar,
                            name,
                            following,
                            followers,
                            company,
                            repo,
                            country
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                binding.rvListSearch.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.errorLayout.visibility = View.INVISIBLE
                showToRecyclerView()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val err = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                binding.progressBar.visibility = View.INVISIBLE
                binding.rvListSearch.visibility = View.INVISIBLE
                binding.errorLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.errorMessage.text = resources.getString(R.string.error_occured)
                Toast.makeText(this@MainActivity, err, Toast.LENGTH_LONG).show()
            }
        })
    }
}