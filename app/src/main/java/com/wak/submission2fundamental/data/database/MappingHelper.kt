package com.wak.submission2fundamental.data.database

import android.database.Cursor
import com.wak.submission2fundamental.data.UserFavorite

object MappingHelper {
    fun mapCursorToArrayList(userFavoriteCursor: Cursor?): ArrayList<UserFavorite>{
        val userFavoriteList = ArrayList<UserFavorite>()
        userFavoriteCursor?.apply {
            while(moveToNext()){
                val login = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.USERNAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserFavColumns.AVATAR))
                userFavoriteList.add(UserFavorite(login, avatar))
            }
        }
        return userFavoriteList
    }
}