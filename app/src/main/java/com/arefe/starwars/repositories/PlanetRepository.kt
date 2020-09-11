package com.arefe.starwars.repositories

import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.RxJavaNetworkBoundResource
import com.arefe.starwars.api.model.Planet
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanetRepository @Inject constructor(private val apiService: ApiService){

    fun getPlanetDetails(url: String) : Observable<ApiResponse<Planet>> {
        return object: RxJavaNetworkBoundResource<Planet, Planet>() {
            override fun createCall(): Single<Result<Planet>> {
                return apiService.getPlanetDetails(url)
            }

            override fun saveCallResult(item: Planet) {

            }

            override fun loadData(response: Planet): ApiResponse<Planet> {
                return ApiResponse.success(response)
            }

        }.asObservable()
    }
}