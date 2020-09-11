package com.arefe.starwars.utilities

import android.view.View

fun View.toVisible(){
    if(this.visibility != View.VISIBLE){
        this.visibility = View.VISIBLE
    }
}

fun View.toGone(){
    if(this.visibility != View.GONE){
        this.visibility = View.GONE
    }
}