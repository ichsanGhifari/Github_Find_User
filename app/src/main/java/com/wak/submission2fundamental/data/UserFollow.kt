package com.wak.submission2fundamental.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFollow (
    var username : String? = null,
    var avatar : String? = null,
) : Parcelable