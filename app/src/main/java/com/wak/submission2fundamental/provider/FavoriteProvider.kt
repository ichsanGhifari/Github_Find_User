package com.wak.submission2fundamental.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.wak.submission2fundamental.data.database.DatabaseContract.UserFavColumns.Companion.AUTHORITY
import com.wak.submission2fundamental.data.database.DatabaseContract.UserFavColumns.Companion.TABLE_NAME
import com.wak.submission2fundamental.data.database.UserFavoriteHelper

class FavoriteProvider : ContentProvider() {
    companion object {
        private const val FAV = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favoriteHelper: UserFavoriteHelper

        init {
            uriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV)
        }
    }
    override fun onCreate(): Boolean {
        favoriteHelper = UserFavoriteHelper.getInstance(context as Context)
        favoriteHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when(uriMatcher.match(uri)){
            FAV -> favoriteHelper.queryAll()
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}