package com.wak.submission2fundamental.data.database

import android.net.Uri
import android.provider.BaseColumns


class DatabaseContract {
    internal class UserFavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val USERNAME = "username"
            const val AVATAR = "avatar"

            const val SCHEME = "content"
            const val AUTHORITY = "com.wak.submission2fundamental"

            val CONTENT_URI: Uri = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}