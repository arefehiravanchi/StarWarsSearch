package com.arefe.starwars.api

import com.arefe.starwars.utilities.ERROR_CONNECTION_PROBLEM
import com.arefe.starwars.utilities.ERROR_NULL_RESPONSE
import com.arefe.starwars.utilities.ERROR_UNKNOWN
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.net.UnknownHostException

class ApiResponseParser {

    fun <ResponseType> parseApiResult(apiResult: Result<ResponseType>?) : ApiResponse<ResponseType> {

        return when {
            apiResult == null -> { ApiResponse.error(ERROR_NULL_RESPONSE) }
            apiResult.error() is UnknownHostException -> { ApiResponse.error(ERROR_CONNECTION_PROBLEM) }
            else -> { parseApiResponse(apiResult.response()) }
        }
    }


    private fun <ResponseType> parseApiResponse(response: Response<ResponseType>?) : ApiResponse<ResponseType> {
        if (response != null && response.isSuccessful) {
            val bodyResponse = response.body()
            return if (bodyResponse != null) {
                ApiResponse.success(bodyResponse)
            } else {
                return ApiResponse.empty()
            }

        } else {
            var errorMessage = response?.errorBody()?.string()
            if (errorMessage.isNullOrEmpty()) {
                errorMessage = response?.message()
            }
            return ApiResponse.error(errorMessage ?: ERROR_UNKNOWN)
        }

    }
}