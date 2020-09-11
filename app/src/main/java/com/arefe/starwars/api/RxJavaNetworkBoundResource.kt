package com.arefe.starwars.api

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.arefe.starwars.utilities.ERROR_UNKNOWN
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber

abstract class RxJavaNetworkBoundResource <ResultType,ResponseType> {

    private val result = BehaviorSubject.create<ApiResponse<ResultType>>()

    init {
        result.onNext(ApiResponse.loading())
        fetchDataFromNetwork()
    }

    private fun fetchDataFromNetwork() {
        val apiResult = createCall()
        apiResult.subscribe(object : SingleObserver<Result<ResponseType>> {
            override fun onSuccess(t: Result<ResponseType>) {
                val parsedResponse = ApiResponseParser().parseApiResult(t)
                when (parsedResponse.status) {
                    Status.SUCCESS -> {
                        saveCallResult(parsedResponse.data!!)
                        result.onNext(loadData(parsedResponse.data))
                    }
                    Status.ERROR -> {
                        val errorMessage = parsedResponse.message
                        result.onNext(ApiResponse.error(errorMessage ?: ERROR_UNKNOWN))
                    }
                }
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                Timber.e("Unsuccessful network call. error : %s ", e.message)
                result.onNext(ApiResponse.error(e.message ?: ""))

            }

        })

    }


    @MainThread
    protected abstract fun createCall(): Single<Result<ResponseType>>

    @WorkerThread
    protected abstract fun saveCallResult(item: ResponseType)

    protected abstract fun loadData(response: ResponseType): ApiResponse<ResultType>

    fun asObservable(): Observable<ApiResponse<ResultType>> {
        return result
    }

}
