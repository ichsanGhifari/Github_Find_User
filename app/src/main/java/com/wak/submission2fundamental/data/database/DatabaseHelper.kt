package com.wak.submission2fundamental.data.database
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wak.submission2fundamental.data.database.DatabaseContract.UserFavColumns.Companion.TABLE_NAME

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    DATABASER_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "dbuserfavorite"
        private const val DATABASER_VERSION = 1
        private const val SQL_CREATE_TABLE_USER_FAVORITE = "CREATE TABLE $TABLE_NAME" +
                "(${DatabaseContract.UserFavColumns.USERNAME} TEXT PRIMARY KEY NOT NULL,"+
                "${DatabaseContract.UserFavColumns.AVATAR} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SQL_CREATE_TABLE_USER_FAVORITE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}