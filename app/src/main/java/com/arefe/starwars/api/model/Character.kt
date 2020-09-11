package com.arefe.starwars.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Character(val name:String, val height:String, val birth_year:String,val gender:String,
                     val homeworld:String?,
                     val films:ArrayList<String>,
                     val species:ArrayList<String>) : Parcelable