package com.wak.submission2fundamental.ui.detailuser

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.adapter.SectionPagerAdapter
import com.wak.submission2fundamental.data.MainData
import com.wak.submission2fundamental.data.User
import com.wak.submission2fundamental.data.UserFavorite
import com.wak.submission2fundamental.data.database.DatabaseContract
import com.wak.submission2fundamental.data.database.MappingHelper
import com.wak.submission2fundamental.data.database.UserFavoriteHelper
import com.wak.submission2fundamental.databinding.ActivityDetailUserBinding
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val GITUSER_DATA = "extra_gituser"
        const val GITUSER_FAV = "extra_fav"
        private val TAG = DetailUserActivity::class.java.simpleName

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.following,
            R.string.followers
        )
    }
    private lateinit var userFavoriteHelper: UserFavoriteHelper
    private lateinit var gitUserData: User
    private var gitUserFavorite: UserFavorite? = null
    private lateinit var binding: ActivityDetailUserBinding

    private var isFavorite: Boolean = false
    private var usernameDb: String? = null
    private var avatarDb: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userFavoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
        userFavoriteHelper.open()

        binding.btnFavorite.setOnClickListener(this)

        //gitUserData = intent.getParcelableExtra(GITUSER_DATA)!!
        val username =chooseParcelable()
        getDetailUser(username) // get detail data by username

        setUpViewPager()

        checkExistDatabase(username!!) // check data are listed in database or not
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun chooseParcelable(): String? {
        var login :String? = null
        gitUserFavorite = intent.getParcelableExtra(GITUSER_FAV)
        if(gitUserFavorite != null){
            login = gitUserFavorite!!.username
        }
        else{
            gitUserData = intent.getParcelableExtra(GITUSER_DATA)!!
            login = gitUserData.username
        }
        return login
    }


    private fun setUpViewPager() {
        val sectionPagerAdapter = SectionPagerAdapter(this)
        binding.viewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }


    private fun hideProfile(state: Boolean) {
        if (state) {
            binding.detailProgressbar.visibility = View.VISIBLE
            binding.detailUsername.visibility = View.INVISIBLE
            binding.detailRepocompany.visibility = View.INVISIBLE
            binding.detailFollow.visibility = View.INVISIBLE
            binding.detailName.visibility = View.INVISIBLE
            binding.locationLayout.visibility = View.INVISIBLE
            binding.viewPager.visibility = View.INVISIBLE
        } else {
            binding.detailProgressbar.visibility = View.INVISIBLE
            binding.detailUsername.visibility = View.VISIBLE
            binding.detailRepocompany.visibility = View.VISIBLE
            binding.detailFollow.visibility = View.VISIBLE
            binding.detailName.visibility = View.VISIBLE
            binding.locationLayout.visibility = View.VISIBLE
            binding.viewPager.visibility = View.VISIBLE
        }
    }

    private fun getDetailUser(username: String?) {
        hideProfile(true)
        val actionbar = supportActionBar
        actionbar!!.title = username
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token ${MainData.gitToken}")
        val urlUser = "https://api.github.com/users/$username"
        client.get(urlUser, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                hideProfile(false)
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

                    usernameDb = username
                    avatarDb = avatar

                    actionbar.title = username
                    addImage(avatar)
                    actionbar.setDisplayHomeAsUpEnabled(true)
                    binding.detailName.text = name
                    binding.detailFollow.text =
                        resources.getString(R.string.follow, following, followers)
                    if (company == "null") binding.detailRepocompany.text =
                        resources.getString(R.string.detail_repocomnull, repo)
                    else binding.detailRepocompany.text =
                        resources.getString(R.string.detail_companyandrepo, company, repo)
                    binding.detailUsername.text = "$username"
                    if (country == "null") binding.locationLayout.visibility = View.INVISIBLE
                    else binding.detailCountry.text = "$country"

                } catch (e: Exception) {
                    Toast.makeText(this@DetailUserActivity, e.message, Toast.LENGTH_SHORT)
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
                Toast.makeText(this@DetailUserActivity, err, Toast.LENGTH_LONG).show()

            }
        })
    }

    private fun addImage(avatar: String?) {
        Glide.with(this)
            .load(avatar)
            .into(binding.detailAvatar)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_favorite -> addRemoveFavorite()
        }
    }

    private fun addRemoveFavorite() {
        if (isFavorite) {
            userFavoriteHelper.removeFromDatabase(usernameDb).toLong()
            isFavorite = false
        }

        else{
            val values = ContentValues()
            values.put(DatabaseContract.UserFavColumns.AVATAR, avatarDb)
            values.put(DatabaseContract.UserFavColumns.USERNAME, usernameDb)

            userFavoriteHelper.addToDatabase(values)
            isFavorite = true

        }
        favoriteDisplayButtonResources()
    }
    private fun checkExistDatabase(username: String){
        GlobalScope.launch(Dispatchers.Main) {
            val deferredUser =  async (Dispatchers.IO){
                val cursor = userFavoriteHelper.queryByUsername(username)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorite = deferredUser.await()
            if(favorite.size > 0){
                isFavorite = true
            }
            favoriteDisplayButtonResources()
        }
    }

    private fun favoriteDisplayButtonResources() {
        if (isFavorite) {
            binding.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_is_true)
        } else {
            binding.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_is_false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        userFavoriteHelper.close()

    }

}