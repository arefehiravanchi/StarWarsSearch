package com.arefe.starwars.api

import com.arefe.starwars.utilities.DATA_LOADING_ERROR
import io.reactivex.functions.Consumer


abstract class ResourceConsumer<T> : Consumer<ApiResponse<T>> {

    abstract fun onSuccess(data: T?)
    abstract fun onLoading()
    abstract fun onEmpty()
    abstract fun onError(message:String?)

    override fun accept(t: ApiResponse<T>?) {
        if (t == null){
            onError(DATA_LOADING_ERROR)
        } else {
            when(t.status) {
                Status.SUCCESS -> onSuccess(t.data)
                Status.LOADING -> onLoading()
                Status.EMPTY -> onEmpty()
                Status.ERROR -> onError(t.message)
            }

        }
    }
}