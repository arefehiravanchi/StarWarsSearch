package com.arefe.starwars.api

import com.arefe.starwars.api.model.*
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET()
    fun getCharacters(@Url url:String,@Query("search") searchQuery: String): Single<Result<BaseListResponse<Character>>>

    @GET
    fun getMovieDetails(@Url url: String) : Single<Result<Movie>>

    @GET
    fun getSpeciesDetails(@Url url: String) : Single<Result<Species>>

    @GET
    fun getPlanetDetails(@Url url: String) : Single<Result<Planet>>
}