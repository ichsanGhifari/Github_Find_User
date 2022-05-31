package com.wak.submission2fundamental.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFavorite (
    var username: String? = null,
    var avatar: String? = null
) : Parcelable