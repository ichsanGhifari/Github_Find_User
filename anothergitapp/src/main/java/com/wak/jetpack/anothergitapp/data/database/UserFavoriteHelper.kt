package com.wak.jetpack.anothergitapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.wak.jetpack.anothergitapp.data.database.DatabaseContract.UserFavColumns.Companion.TABLE_NAME
import com.wak.jetpack.anothergitapp.data.database.DatabaseContract.UserFavColumns.Companion.USERNAME

class UserFavoriteHelper(context: Context) {
    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private var database: SQLiteDatabase = dataBaseHelper.writableDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME

        private var INSTANCE: UserFavoriteHelper? = null
        fun getInstance(context: Context): UserFavoriteHelper = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserFavoriteHelper(context)
        }

    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    //read all data from database
    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$USERNAME ASC",
            null
        )

    }

    //get data using username
    fun queryByUsername(login: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$USERNAME= ?",
            arrayOf(login),
            null,
            null,
            null,
        )
    }

    fun addToDatabase(values: ContentValues): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    //delete data by username
    fun removeFromDatabase(login: String?):Int{
        return database.delete(DATABASE_TABLE,"$USERNAME = '$login'",null)
    }

}