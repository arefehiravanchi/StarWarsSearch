package com.arefe.starwars.ui.characterdetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arefe.starwars.R
import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ResourceConsumer
import com.arefe.starwars.api.model.Movie
import com.arefe.starwars.api.model.Planet
import com.arefe.starwars.api.model.SpeciesWithPlanet
import com.arefe.starwars.repositories.MovieRepository
import com.arefe.starwars.repositories.PlanetRepository
import com.arefe.starwars.repositories.SpeciesRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CharacterDetailViewModel @Inject constructor(
    private val context: Context,
    private val movieRepository: MovieRepository,
    private val speciesRepository: SpeciesRepository,
    private val planetRepository: PlanetRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val movieList = MutableLiveData<ApiResponse<ArrayList<Movie>>>()
    private val speciesList = MutableLiveData<ApiResponse<ArrayList<SpeciesWithPlanet>>>()
    private val planet = MutableLiveData<ApiResponse<Planet>>()


    fun getMovieListResponse(): MutableLiveData<ApiResponse<ArrayList<Movie>>> = movieList

    fun getSpeciesListResponse(): MutableLiveData<ApiResponse<ArrayList<SpeciesWithPlanet>>> = speciesList

    fun getPlanetResponse():MutableLiveData<ApiResponse<Planet>> = planet

    fun getCharacterMovies(movieUrls: ArrayList<String>) {
        compositeDisposable.add(movieRepository.getMovieDetail(movieUrls)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ResourceConsumer<ArrayList<Movie>>() {
                override fun onSuccess(data: ArrayList<Movie>?) {
                    if (data != null && data.isNotEmpty()) {
                        movieList.value = ApiResponse.success(data)
                    } else {
                        movieList.value = ApiResponse.empty()
                    }
                }

                override fun onLoading() {
                    movieList.value = ApiResponse.loading()
                }

                override fun onEmpty() {
                    movieList.value = ApiResponse.empty()
                }

                override fun onError(message: String?) {
                    movieList.value = ApiResponse.error(message ?: context.getString(R.string.error_loading_movie))
                }

            }))

    }

    fun getCharacterSpecies(speciesUrl:ArrayList<String>) {
        if (speciesUrl.isEmpty()) {
            return
        }
        compositeDisposable.add(speciesRepository.getSpeciesDetails(speciesUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ResourceConsumer<ArrayList<SpeciesWithPlanet>>() {
                override fun onSuccess(data: ArrayList<SpeciesWithPlanet>?) {
                    if (data != null && data.isNotEmpty()) {
                        speciesList.value = ApiResponse.success(data)
                    } else {
                        speciesList.value = ApiResponse.empty()
                    }
                }

                override fun onLoading() {
                    speciesList.value = ApiResponse.loading()
                }

                override fun onEmpty() {
                    speciesList.value = ApiResponse.empty()
                }

                override fun onError(message: String?) {
                    speciesList.value = ApiResponse.error(message ?: context.getString(R.string.error_loading_species))
                }

            })
        )
    }

    fun getCharacterPlanet(planetUrl:String) {
        compositeDisposable.add(planetRepository.getPlanetDetails(planetUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ResourceConsumer<Planet>(){
                override fun onSuccess(data: Planet?) {
                    if (data == null){
                        planet.value = ApiResponse.empty()
                    } else{
                        planet.value = ApiResponse.success(data)
                    }
                }

                override fun onLoading() {
                    planet.value = ApiResponse.loading()
                }

                override fun onEmpty() {
                    planet.value = ApiResponse.empty()
                }

                override fun onError(message: String?) {
                    planet.value = ApiResponse.error(message ?: context.getString(R.string.error_loading_planet))
                }

            }))
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}