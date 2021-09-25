package com.example.messager.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var uid : String, var email :String, var username : String, var profileImageUrl : String): Parcelable {
    constructor():this("","","","")

}

