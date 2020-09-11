package com.arefe.starwars.ui.characterdetail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.ResourceObserver
import com.arefe.starwars.api.Status
import com.arefe.starwars.api.model.Character
import com.arefe.starwars.api.model.Movie
import com.arefe.starwars.api.model.Planet
import com.arefe.starwars.api.model.SpeciesWithPlanet
import com.arefe.starwars.di.Injectable
import com.arefe.starwars.ui.characterdetail.model.*
import kotlinx.android.synthetic.main.activity_character_details.*
import javax.inject.Inject

class CharacterDetailActivity : AppCompatActivity(), Injectable {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var characterDetailsViewModel: CharacterDetailViewModel? = null

    private val characterItems = arrayListOf<CharacterListItem>()
    var characterDetailAdapter: CharacterDetailAdapter? = null
    private val characterPlanet = CharacterPlanetInfo("","", Status.LOADING)
    private val characterSpecies = CharacterSpeciesInfo(arrayListOf(),Status.LOADING)
    private val characterLoadingErrorStatusMovie = CharacterMovieListErrorItem("",Status.LOADING)

    companion object {
        const val EXT_CHARACTER = "ext_character"
        fun start(activity: AppCompatActivity, character: Character) {
            val intent = Intent(activity, CharacterDetailActivity::class.java)
            intent.putExtra(EXT_CHARACTER, character)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        characterDetailsViewModel = ViewModelProvider(this,viewModelFactory)[CharacterDetailViewModel::class.java]
        val character = getCharacter()
        if (character != null){
            initViews(character)
            observeData()
            fetchData(character)
        }  else {
        //todo : do something is good for wrong parameter
        }

    }

    private fun getCharacter(): Character? {
        return intent.getParcelableExtra(EXT_CHARACTER)
    }

    private fun initViews(character: Character) {

        activity_characterDetails_txtName.text = character.name

        characterItems.add(CharacterListTitle(getString(R.string.personal_info)))
        characterItems.add(CharacterPersonalInfo(character.name,character.birth_year,character.height,character.gender))
        if (!character.homeworld.isNullOrBlank()){
            characterItems.add(CharacterListTitle(getString(R.string.planet_info)))
            characterItems.add(characterPlanet)
        }

        if (!character.species.isNullOrEmpty()){
            characterItems.add(CharacterListTitle(getString(R.string.species_info)))
            characterItems.add(characterSpecies)
        }
        characterItems.add(CharacterListTitle(getString(R.string.movies_list)))
        characterItems.add(characterLoadingErrorStatusMovie)

        characterDetailAdapter = CharacterDetailAdapter(this,characterItems)
        activity_character_rcCharacterItems.apply {
            adapter = characterDetailAdapter
            layoutManager = LinearLayoutManager(this@CharacterDetailActivity,RecyclerView.VERTICAL,false)
        }

    }

    private fun fetchData(character: Character){
        characterDetailsViewModel?.getCharacterMovies(character.films)
        if (character.species.isNotEmpty()) {
            characterDetailsViewModel?.getCharacterSpecies(character.species)
        }
        if (!character.homeworld.isNullOrEmpty()) {
            characterDetailsViewModel?.getCharacterPlanet(character.homeworld)
        }
    }

    private fun observeData() {
        observeMovies()
        observeSpecies()
        observePlanet()
    }

    private fun observeMovies(){
        characterDetailsViewModel?.getMovieListResponse()?.observe(this,object : ResourceObserver<ArrayList<Movie>>(){
            override fun onSuccess(data: ArrayList<Movie>?) {
                data?.let { movieList ->
                    movieList.map {
                        characterItems.add(CharacterMovieItem(it.title,it.opening_crawl))
                    }
                    characterItems.remove(characterLoadingErrorStatusMovie)
                    characterDetailAdapter?.notifyDataSetChanged()
                }
            }

            override fun onLoading() {
            }

            override fun onEmpty() {
                characterLoadingErrorStatusMovie.status = Status.ERROR
                characterLoadingErrorStatusMovie.errorMessage = getString(R.string.error_loading_movie)
                characterDetailAdapter?.notifyDataSetChanged()

            }

            override fun onError(message: String?) {
                characterLoadingErrorStatusMovie.errorMessage = getString(R.string.error_loading_movie)
                characterLoadingErrorStatusMovie.status = Status.ERROR
                characterDetailAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun observeSpecies() {
        characterDetailsViewModel?.getSpeciesListResponse()?.observe(this, object : ResourceObserver<ArrayList<SpeciesWithPlanet>>(){
            override fun onSuccess(data: ArrayList<SpeciesWithPlanet>?) {
                data?.let {
                    characterSpecies.species.addAll(it)
                    characterSpecies.status = Status.SUCCESS
                    characterDetailAdapter?.notifyDataSetChanged()
                }
            }

            override fun onLoading() {
            }

            override fun onEmpty() {
                characterSpecies.status = Status.ERROR
                characterDetailAdapter?.notifyDataSetChanged()
            }

            override fun onError(message: String?) {
                characterSpecies.status = Status.ERROR
                characterDetailAdapter?.notifyDataSetChanged()
            }

        })

    }

    private fun observePlanet(){
        characterDetailsViewModel?.getPlanetResponse()?.observe(this, object : ResourceObserver<Planet>(){
            override fun onSuccess(data: Planet?) {
                data?.let {
                    characterPlanet.name = it.name
                    characterPlanet.population = it.population
                    characterPlanet.status = Status.SUCCESS
                    characterDetailAdapter?.notifyDataSetChanged()
                }
            }

            override fun onLoading() {}

            override fun onEmpty() {
                characterPlanet.status = Status.ERROR
                characterDetailAdapter?.notifyDataSetChanged()
            }

            override fun onError(message: String?) {
                characterPlanet.status = Status.ERROR
                characterDetailAdapter?.notifyDataSetChanged()
            }

        })

    }
}