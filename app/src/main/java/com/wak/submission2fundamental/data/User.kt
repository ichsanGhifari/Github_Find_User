package com.wak.submission2fundamental.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var username : String? = null,
        var avatar : String? = null,
        var name: String? = null,
        var following : Int? = null,
        var followers: Int? = null,
        var company : String? = null,
        var repo : Int? = null,
        var country : String? = null

) : Parcelable
