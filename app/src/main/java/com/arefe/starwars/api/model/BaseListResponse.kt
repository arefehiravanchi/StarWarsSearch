package com.arefe.starwars.api.model


data class BaseListResponse<T>(val count:Int,val next:String?,val previous:String?,val results:ArrayList<T>)