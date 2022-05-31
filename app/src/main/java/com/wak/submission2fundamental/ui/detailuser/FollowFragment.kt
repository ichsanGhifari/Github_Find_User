package com.wak.submission2fundamental.ui.detailuser
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.adapter.FollowUserAdapter
import com.wak.submission2fundamental.data.MainData
import com.wak.submission2fundamental.data.User
import com.wak.submission2fundamental.data.UserFavorite
import com.wak.submission2fundamental.data.UserFollow
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowFragment : Fragment() {
    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        const val GITUSER_DATA_FOLLOW = "extra_gituser_follow"

        const val GITUSER_FAV_FOLLOW = "extra_fav"
        private val TAG = FollowFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(index: Int) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                }
            }
    }
    private var gitFollowUserFavorite: UserFavorite? = null
    private lateinit var gitFollowUserData: User
    private var gituserFollowData : ArrayList<UserFollow> = ArrayList()
    private lateinit var adapter: FollowUserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FollowUserAdapter(gituserFollowData)
        gituserFollowData.clear()
        val username = chooseParcelableFollow()
        val index = arguments?. getInt(ARG_SECTION_NUMBER,0)
       getListFollow(username,index)
    }

    private fun chooseParcelableFollow(): String? {
        var login :String? = null
        gitFollowUserFavorite = activity!!.intent.getParcelableExtra(DetailUserActivity.GITUSER_FAV)
        if(gitFollowUserFavorite != null){
            login = gitFollowUserFavorite!!.username
        }
        else{
            gitFollowUserData = activity!!.intent.getParcelableExtra(DetailUserActivity.GITUSER_DATA)!!
            login = gitFollowUserData.username
        }
        return login
    }
    private fun getListFollow(userName: String?, index: Int?) {
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token ${MainData.gitToken}")
        var urlUser : String? = null
        if(index == 1) {urlUser = "https://api.github.com/users/$userName/following"}
        else { urlUser = "https://api.github.com/users/$userName/followers"}
        val progressBar : ProgressBar = view!!.findViewById(R.id.progressBar_detail)
        progressBar.visibility = VISIBLE
        client.get(urlUser, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val jsonUserArray = JSONArray(result)
                    for (i in 0 until jsonUserArray.length()){
                        val username = jsonUserArray.getJSONObject(i).getString("login")
                        val avatar = jsonUserArray.getJSONObject(i).getString("avatar_url")
                        gituserFollowData.add(UserFollow(username,avatar))
                    }

                } catch (e: Exception) {
                    makeText(activity, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                progressBar.visibility = INVISIBLE
                showRecyclerListFollower()
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {

                progressBar.visibility =INVISIBLE
                makeText(activity, statusCode, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun showRecyclerListFollower() {
        val rvListFollow : RecyclerView = view!!.findViewById(R.id.rv_list_follow)
        rvListFollow.layoutManager = LinearLayoutManager(activity)
        rvListFollow.adapter = adapter
    }


}

