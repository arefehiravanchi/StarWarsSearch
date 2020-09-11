package com.arefe.starwars.repositories

import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.RxJavaNetworkBoundResource
import com.arefe.starwars.api.model.BaseListResponse
import com.arefe.starwars.api.model.Character
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(private val apiService: ApiService) {

    fun getCharacters(searchUrl:String,searchQuery: String): Observable<ApiResponse<BaseListResponse<Character>>> {
        return object :
            RxJavaNetworkBoundResource<BaseListResponse<Character>, BaseListResponse<Character>>() {
            override fun createCall(): Single<Result<BaseListResponse<Character>>> {
                return apiService.getCharacters(searchUrl,searchQuery)
            }

            override fun saveCallResult(item: BaseListResponse<Character>) {
            }

            override fun loadData(response: BaseListResponse<Character>): ApiResponse<BaseListResponse<Character>> {
                return ApiResponse.success(response)
            }

        }.asObservable()

    }

}