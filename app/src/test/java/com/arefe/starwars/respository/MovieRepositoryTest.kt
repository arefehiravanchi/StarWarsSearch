package com.arefe.starwars.respository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.arefe.starwars.RxImmediateSchedulerRule
import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ApiService
import com.arefe.starwars.api.model.Movie
import com.arefe.starwars.repositories.MovieRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

class MovieRepositoryTest {

    private val compositeDisposable = CompositeDisposable()

    @Mock
    lateinit var apiService: ApiService

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    lateinit var movieRepository: MovieRepository

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        movieRepository = MovieRepository(apiService)
    }

    @Test
    fun test_getMovies(){
        val sampleMovieUrls = arrayListOf<String>()
        sampleMovieUrls.add("http://swapi.dev/api/films/1/")
        sampleMovieUrls.add("http://swapi.dev/api/films/2/")

        val jsonResponseMovie1 = "{\n" +
                "    \"title\": \"A New Hope\", \n" +
                "    \"episode_id\": 4, \n" +
                "    \"opening_crawl\": \"It is a period of civil war.\\r\\nRebel spaceships, striking\\r\\nfrom a hidden base, have won\\r\\ntheir first victory against\\r\\nthe evil Galactic Empire.\\r\\n\\r\\nDuring the battle, Rebel\\r\\nspies managed to steal secret\\r\\nplans to the Empire's\\r\\nultimate weapon, the DEATH\\r\\nSTAR, an armored space\\r\\nstation with enough power\\r\\nto destroy an entire planet.\\r\\n\\r\\nPursued by the Empire's\\r\\nsinister agents, Princess\\r\\nLeia races home aboard her\\r\\nstarship, custodian of the\\r\\nstolen plans that can save her\\r\\npeople and restore\\r\\nfreedom to the galaxy....\", \n" +
                "    \"director\": \"George Lucas\", \n" +
                "    \"producer\": \"Gary Kurtz, Rick McCallum\", \n" +
                "    \"release_date\": \"1977-05-25\", \n" +
                "    \"characters\": [\n" +
                "        \"http://swapi.dev/api/people/1/\"\n" +
                "    ], \n" +
                "    \"planets\": [\n" +
                "        \"http://swapi.dev/api/planets/1/\"\n" +
                "    ], \n" +
                "    \"starships\": [\n" +
                "        \"http://swapi.dev/api/starships/2/\"\n" +
                "    ], \n" +
                "    \"vehicles\": [\n" +
                "        \"http://swapi.dev/api/vehicles/4/\"\n" +
                "    ], \n" +
                "    \"species\": [\n" +
                "        \"http://swapi.dev/api/species/1/\"\n" +
                "    ], \n" +
                "    \"created\": \"2014-12-10T14:23:31.880000Z\", \n" +
                "    \"edited\": \"2014-12-20T19:49:45.256000Z\", \n" +
                "    \"url\": \"http://swapi.dev/api/films/1/\"\n" +
                "}"


        val jsonResponseMovie2 = "{\n" +
                "  \"title\": \"The Empire Strikes Back\",\n" +
                "  \"episode_id\": 5,\n" +
                "  \"opening_crawl\": \"It is a dark time for the\\r\\nRebellion. Although the Death\\r\\nStar has been destroyed,\\r\\nImperial troops have driven the\\r\\nRebel forces from their hidden\\r\\nbase and pursued them across\\r\\nthe galaxy.\\r\\n\\r\\nEvading the dreaded Imperial\\r\\nStarfleet, a group of freedom\\r\\nfighters led by Luke Skywalker\\r\\nhas established a new secret\\r\\nbase on the remote ice world\\r\\nof Hoth.\\r\\n\\r\\nThe evil lord Darth Vader,\\r\\nobsessed with finding young\\r\\nSkywalker, has dispatched\\r\\nthousands of remote probes into\\r\\nthe far reaches of space....\",\n" +
                "  \"director\": \"Irvin Kershner\",\n" +
                "  \"producer\": \"Gary Kurtz, Rick McCallum\",\n" +
                "  \"release_date\": \"1980-05-17\",\n" +
                "  \"characters\": [\n" +
                "    \"http://swapi.dev/api/people/1/\"\n" +
                "  ],\n" +
                "  \"planets\": [\n" +
                "    \"http://swapi.dev/api/planets/4/\"\n" +
                "  ],\n" +
                "  \"starships\": [\n" +
                "    \"http://swapi.dev/api/starships/3/\"\n" +
                "  ],\n" +
                "  \"vehicles\": [\n" +
                "    \"http://swapi.dev/api/vehicles/8/\"\n" +
                "  ],\n" +
                "  \"species\": [\n" +
                "    \"http://swapi.dev/api/species/1/\"\n" +
                "  ],\n" +
                "  \"created\": \"2014-12-12T11:26:24.656000Z\",\n" +
                "  \"edited\": \"2014-12-15T13:07:53.386000Z\",\n" +
                "  \"url\": \"http://swapi.dev/api/films/2/\"\n" +
                "}"

        val sampleMovieItem1 = Gson().fromJson(jsonResponseMovie1,Movie::class.java)
        val sampleMovieItem2 = Gson().fromJson(jsonResponseMovie2,Movie::class.java)
        Mockito.`when`(apiService.getMovieDetails(sampleMovieUrls[0])).thenReturn(Single.just(Result.response(Response.success(sampleMovieItem1))))
        Mockito.`when`(apiService.getMovieDetails(sampleMovieUrls[1])).thenReturn(Single.just(Result.response(Response.success(sampleMovieItem2))))

        val expectedMovieList = arrayListOf<Movie>()
        expectedMovieList.add(sampleMovieItem1)
        expectedMovieList.add(sampleMovieItem2)

        val movieListResponse = movieRepository.getMovieDetail(sampleMovieUrls).test()
        compositeDisposable.add(movieListResponse)

        movieListResponse.assertNoErrors()
        movieListResponse.assertValue(ApiResponse.success(expectedMovieList))
    }

    @After
    fun tearsDown(){
        compositeDisposable.clear()
    }
}