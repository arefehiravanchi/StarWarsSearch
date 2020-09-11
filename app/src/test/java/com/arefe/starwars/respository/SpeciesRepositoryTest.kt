package com.arefe.starwars.respository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.arefe.starwars.RxImmediateSchedulerRule
import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.model.Planet
import com.arefe.starwars.api.model.Species
import com.arefe.starwars.api.model.SpeciesWithPlanet
import com.arefe.starwars.repositories.SpeciesRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

class SpeciesRepositoryTest {

    private val compositeDisposable = CompositeDisposable()

    @Mock
    lateinit var apiService: ApiService

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    lateinit var speciesRepository: SpeciesRepository

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        speciesRepository = SpeciesRepository(apiService)
    }

    @Test
    fun test_getSpecies_WithPlanet(){
        val sampleSpeciesUrls = arrayListOf<String>()
        sampleSpeciesUrls.add("https://swapi.dev/api/species/3/")

        val jsonResponseSpecies = "{\n" +
                "    \"name\": \"Wookie\", \n" +
                "    \"classification\": \"mammal\", \n" +
                "    \"designation\": \"sentient\", \n" +
                "    \"average_height\": \"210\", \n" +
                "    \"skin_colors\": \"gray\", \n" +
                "    \"hair_colors\": \"black, brown\", \n" +
                "    \"eye_colors\": \"blue, green, yellow, brown, golden, red\", \n" +
                "    \"average_lifespan\": \"400\", \n" +
                "    \"homeworld\": \"http://swapi.dev/api/planets/14/\", \n" +
                "    \"language\": \"Shyriiwook\", \n" +
                "    \"people\": [\n" +
                "        \"http://swapi.dev/api/people/13/\"\n" +
                "        ], \n" +
                "    \"films\": [\n" +
                "        \"http://swapi.dev/api/films/1/\"\n" +
                "    ], \n" +
                "    \"created\": \"2014-12-10T16:44:31.486000Z\", \n" +
                "    \"edited\": \"2014-12-20T21:36:42.142000Z\", \n" +
                "    \"url\": \"http://swapi.dev/api/species/3/\"\n" +
                "}"

        val jsonResponsePlanet = "{\n" +
                "    \"name\": \"Kashyyyk\", \n" +
                "    \"rotation_period\": \"26\", \n" +
                "    \"orbital_period\": \"381\", \n" +
                "    \"diameter\": \"12765\", \n" +
                "    \"climate\": \"tropical\", \n" +
                "    \"gravity\": \"1 standard\", \n" +
                "    \"terrain\": \"jungle, forests, lakes, rivers\", \n" +
                "    \"surface_water\": \"60\", \n" +
                "    \"population\": \"45000000\", \n" +
                "    \"residents\": [\n" +
                "        \"http://swapi.dev/api/people/13/\"    ], \n" +
                "    \"films\": [\n" +
                "        \"http://swapi.dev/api/films/6/\"\n" +
                "    ], \n" +
                "    \"created\": \"2014-12-10T13:32:00.124000Z\", \n" +
                "    \"edited\": \"2014-12-20T20:58:18.442000Z\", \n" +
                "    \"url\": \"http://swapi.dev/api/planets/14/\"\n" +
                "}"

        val sampleSpecies = Gson().fromJson(jsonResponseSpecies,Species::class.java)
        val samplePlanet = Gson().fromJson(jsonResponsePlanet,Planet::class.java)
        Mockito.`when`(apiService.getSpeciesDetails(sampleSpeciesUrls[0])).thenReturn(Single.just(Result.response(Response.success(sampleSpecies))))
        Mockito.`when`(apiService.getPlanetDetails(sampleSpecies.homeworld!!)).thenReturn(Single.just(Result.response(Response.success(samplePlanet))))

        val expectedSpeciesList = arrayListOf<SpeciesWithPlanet>()
        expectedSpeciesList.add(SpeciesWithPlanet(sampleSpecies,samplePlanet))

        val speciesListResponse = speciesRepository.getSpeciesDetails(sampleSpeciesUrls).test()
        compositeDisposable.add(speciesListResponse)

        speciesListResponse.assertNoErrors()
        speciesListResponse.assertValue(ApiResponse.success(expectedSpeciesList))
    }


    @Test
    fun test_getSpecies_WithoutPlanet(){
        val sampleSpeciesUrls = arrayListOf<String>()
        sampleSpeciesUrls.add("https://swapi.dev/api/species/2/")

        val jsonResponseSpecies = "{\n" +
                "    \"name\": \"Droid\", \n" +
                "    \"classification\": \"artificial\", \n" +
                "    \"designation\": \"sentient\", \n" +
                "    \"average_height\": \"n/a\", \n" +
                "    \"skin_colors\": \"n/a\", \n" +
                "    \"hair_colors\": \"n/a\", \n" +
                "    \"eye_colors\": \"n/a\", \n" +
                "    \"average_lifespan\": \"indefinite\", \n" +
                "    \"homeworld\": null, \n" +
                "    \"language\": \"n/a\", \n" +
                "    \"people\": [\n" +
                "        \"http://swapi.dev/api/people/2/\"\n" +
                "    ], \n" +
                "    \"films\": [\n" +
                "        \"http://swapi.dev/api/films/1/\"\n" +
                "    ], \n" +
                "    \"created\": \"2014-12-10T15:16:16.259000Z\", \n" +
                "    \"edited\": \"2014-12-20T21:36:42.139000Z\", \n" +
                "    \"url\": \"http://swapi.dev/api/species/2/\"\n" +
                "}"


        val sampleSpecies = Gson().fromJson(jsonResponseSpecies,Species::class.java)
        Mockito.`when`(apiService.getSpeciesDetails(sampleSpeciesUrls[0])).thenReturn(Single.just(Result.response(Response.success(sampleSpecies))))

        val expectedSpeciesList = arrayListOf<SpeciesWithPlanet>()
        expectedSpeciesList.add(SpeciesWithPlanet(sampleSpecies,null))

        val speciesListResponse = speciesRepository.getSpeciesDetails(sampleSpeciesUrls).test()
        compositeDisposable.add(speciesListResponse)

        speciesListResponse.assertNoErrors()
        speciesListResponse.assertValue(ApiResponse.success(expectedSpeciesList))
    }


    @After
    fun tearDown(){
        compositeDisposable.clear()
    }




}