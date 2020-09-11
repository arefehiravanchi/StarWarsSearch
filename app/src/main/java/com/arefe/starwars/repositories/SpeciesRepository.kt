package com.arefe.starwars.repositories

import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.RxJavaNetworkBoundResource
import com.arefe.starwars.api.model.Species
import com.arefe.starwars.api.model.SpeciesWithPlanet
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesRepository @Inject constructor(private val apiService: ApiService){

    fun getSpeciesDetails(urls: ArrayList<String>) : Observable<ApiResponse<ArrayList<SpeciesWithPlanet>>> {
        return object: RxJavaNetworkBoundResource<ArrayList<SpeciesWithPlanet>, ArrayList<SpeciesWithPlanet>>() {
            override fun createCall(): Single<Result<ArrayList<SpeciesWithPlanet>>> {

                val singleSpecieList = arrayListOf<Single<Result<SpeciesWithPlanet>>>()
                urls.map { planetUrl ->
                    singleSpecieList.add(apiService.getSpeciesDetails(planetUrl).flatMap {
                        mapPlanetToSpecies(it)
                    })
                }

                return Single.zip(singleSpecieList) { emittedList ->
                    when {
                        emittedList.any { (it as Result<*>).isError } -> {
                            val errorItem = emittedList.filter { (it as Result<*>).isError }[0]
                            Result.error<ArrayList<SpeciesWithPlanet>>((errorItem as Result<*>).error() ?: Throwable())
                        }

                        else -> {
                            val speciesWithPlanetList = arrayListOf<SpeciesWithPlanet>()
                            emittedList.map {
                                (it as Result<SpeciesWithPlanet>).response()?.body()?.let { item ->
                                    speciesWithPlanetList.add(item)
                                }
                            }

                            Result.response(Response.success(speciesWithPlanetList))
                        }

                    }

                }
            }

            override fun saveCallResult(item: ArrayList<SpeciesWithPlanet>) {
            }

            override fun loadData(response: ArrayList<SpeciesWithPlanet>): ApiResponse<ArrayList<SpeciesWithPlanet>> {
                return ApiResponse.success(response)
            }

        }.asObservable()
    }

    private fun mapPlanetToSpecies(speciesResult :Result<Species>) : Single<Result<SpeciesWithPlanet>> {
        if (checkValidateResult(speciesResult)) {
            val species = speciesResult.response()?.body()
            return if (species?.homeworld != null) {
                apiService.getPlanetDetails(species.homeworld).map { planetResult ->
                    if (checkValidateResult(planetResult)){
                        val planet = planetResult.response()?.body()
                        Result.response(Response.success(SpeciesWithPlanet(species,planet)))
                    } else {
                        Result.response(Response.success(SpeciesWithPlanet(species,null)))
                    }
                }

            } else {
                Single.just(Result.response(Response.success(SpeciesWithPlanet(species!!,null))))
            }
        } else {
            return Single.just(Result.error(speciesResult.error() ?: Throwable()))
        }

    }

    private fun checkValidateResult(result : Result<*>):Boolean{
       return !result.isError && result.response()?.body() != null
    }
}