package com.arefe.starwars.repositories

import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.RxJavaNetworkBoundResource
import com.arefe.starwars.api.model.Movie
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository  @Inject constructor(private val apiService: ApiService) {

    fun getMovieDetail(urls:ArrayList<String>): Observable<ApiResponse<ArrayList<Movie>>> {
        return object : RxJavaNetworkBoundResource<ArrayList<Movie>,ArrayList<Movie>>() {
            override fun createCall(): Single<Result<ArrayList<Movie>>> {
                val singleMovieList = arrayListOf<Single<Result<Movie>>>()
                urls.map { movieUrl ->
                    singleMovieList.add(apiService.getMovieDetails(movieUrl))
                }
                return Single.zip(singleMovieList) { emittedList ->
                    when {
                        emittedList.any { (it as Result<*>).isError } -> {
                            val errorItem = emittedList.filter { (it as Result<*>).isError }[0]
                            Result.error<ArrayList<Movie>>((errorItem as Result<*>).error() ?: Throwable())
                        }

                        else -> {
                            val movieList = arrayListOf<Movie>()
                            emittedList.map {
                                (it as Result<Movie>).response()?.body()?.let { item ->
                                    movieList.add(item)
                                }
                            }

                            Result.response(Response.success(movieList))
                        }
                    }
                }

            }

            override fun saveCallResult(item: ArrayList<Movie>) {
            }

            override fun loadData(response: ArrayList<Movie>): ApiResponse<ArrayList<Movie>> {
                return ApiResponse.success(response)
            }

        }.asObservable()
    }

}