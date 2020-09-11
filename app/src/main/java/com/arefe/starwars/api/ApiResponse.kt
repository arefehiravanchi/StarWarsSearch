package com.arefe.starwars.api

data class ApiResponse<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> empty(): ApiResponse<T> {
            return ApiResponse(
                Status.EMPTY,
                null,
                null
            )
        }

        fun <T> error(msg: String): ApiResponse<T> {
            return ApiResponse(Status.ERROR, null, msg)
        }

        fun <T> loading(): ApiResponse<T> {
            return ApiResponse(
                Status.LOADING,
                null,
                null
            )
        }
    }
}