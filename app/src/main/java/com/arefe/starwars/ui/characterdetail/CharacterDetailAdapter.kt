package com.arefe.starwars.ui.characterdetail

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.Status
import com.arefe.starwars.ui.characterdetail.model.*
import com.arefe.starwars.utilities.Utils
import com.arefe.starwars.utilities.toGone
import com.arefe.starwars.utilities.toVisible
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class CharacterDetailAdapter(val context: Context, private val characterItems:ArrayList<CharacterListItem>) :
    RecyclerView.Adapter<CharacterDetailAdapter.BaseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder{
        return when(viewType){
            CharacterListItem.TYPE_LIST_TITLE -> {
                val view = inflater.inflate(R.layout.item_character_title, parent, false)
                TitleViewHolder(view)
            }
            CharacterListItem.TYPE_PERSONAL_INFO -> {
                val view = inflater.inflate(R.layout.item_character_personal_info, parent, false)
                PersonalInfoViewHolder(view)
            }

            CharacterListItem.TYPE_PLANET_INFO -> {
                val view = inflater.inflate(R.layout.item_character_planet, parent, false)
                PlanetViewHolder(view)
            }

            CharacterListItem.TYPE_SPECIES_INFO -> {
                val view = inflater.inflate(R.layout.item_character_species_list, parent, false)
                SpeciesViewHolder(view)

            }
            CharacterListItem.TYPE_MOVIE -> {
                val view = inflater.inflate(R.layout.item_character_movie, parent, false)
                return MovieViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_character_error_movie_list, parent, false)
                return LoadingErrorMoviesViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return characterItems[position].type
    }

    override fun getItemCount(): Int = characterItems.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(characterItems[position])
    }


    abstract class BaseViewHolder(view:View) : RecyclerView.ViewHolder(view){
        abstract fun bind(item:CharacterListItem)
    }

    class TitleViewHolder(view: View): BaseViewHolder(view){
        private val txtTitle = view.findViewById<TextView>(R.id.item_character_txtTitle)
        override fun bind(item: CharacterListItem) {
            txtTitle.text = (item as CharacterListTitle).title
        }
    }

    inner class PersonalInfoViewHolder(view: View) : BaseViewHolder(view){
        private val txtName = view.findViewById<TextView>(R.id.item_personalInfo_txtName)
        private val txtBirthDate = view.findViewById<TextView>(R.id.item_personalInfo_txtBirthDate)
        private val txtHeight = view.findViewById<TextView>(R.id.item_personalInfo_txtHeight)
        private val txtGender = view.findViewById<TextView>(R.id.item_personalInfo_txtGender)
        override fun bind(item: CharacterListItem) {
            val personalInfoItem = (item as CharacterPersonalInfo)
            txtName.text = context.getString(R.string.formatted_name,personalInfoItem.name)
            txtBirthDate.text = context.getString(R.string.character_birth_date,personalInfoItem.birthDate)
            txtHeight.text = context.getString(R.string.character_height,Utils.getFormattedHeight(personalInfoItem.height))
            txtGender.text = context.getString(R.string.character_gender,personalInfoItem.gender)
        }

    }

    inner class PlanetViewHolder(view: View) : BaseViewHolder(view){
        private val txtPlanetName = view.findViewById<TextView>(R.id.item_character_txtPlanetName)
        private val txtPlanetPopulation = view.findViewById<TextView>(R.id.item_character_txtPopulation)
        private val pbLoading = view.findViewById<MaterialProgressBar>(R.id.item_character_pbPlanetLoading)
        private val txtError = view.findViewById<TextView>(R.id.item_character_txtErrorPlanet)

        override fun bind(item: CharacterListItem) {
            val planetItem = (item as CharacterPlanetInfo)
            when(planetItem.status){
                Status.LOADING -> {
                    pbLoading.toVisible()
                    txtError.toGone()
                    txtPlanetName.toGone()
                    txtPlanetPopulation.toGone()
                }

                Status.SUCCESS -> {
                    pbLoading.toGone()
                    txtError.toGone()
                    txtPlanetName.toVisible()
                    txtPlanetPopulation.toVisible()
                    txtPlanetName.text = context.getString(R.string.formatted_name,planetItem.name)
                    txtPlanetPopulation.text = context.getString(R.string.formatted_population,planetItem.population)
                }

                Status.ERROR ->{
                    pbLoading.toGone()
                    txtError.toVisible()
                    txtPlanetName.toGone()
                    txtPlanetPopulation.toGone()

                }

            }

        }
    }

    inner class SpeciesViewHolder(view: View):BaseViewHolder(view){
        private val pbSpeciesLoading = view.findViewById<MaterialProgressBar>(R.id.item_character_pbSpeciesList)
        private val txtError = view.findViewById<TextView>(R.id.item_character_txtErrorSpeciesList)
        private val rcSpecies = view.findViewById<RecyclerView>(R.id.item_character_rcSpeciesList)
        override fun bind(item: CharacterListItem) {
            val speciesItems = (item as CharacterSpeciesInfo)

            when(speciesItems.status){
                Status.LOADING -> {
                    pbSpeciesLoading.toVisible()
                    txtError.toGone()
                    rcSpecies.toGone()
                }

                Status.SUCCESS -> {
                    pbSpeciesLoading.toGone()
                    txtError.toGone()
                    rcSpecies.toVisible()
                    speciesItems.status
                    val divider = DividerItemDecoration(context, RecyclerView.VERTICAL)
                    divider.setDrawable(ColorDrawable(ContextCompat.getColor(context,R.color.colorAccent)))
                    rcSpecies.apply {
                        adapter = CharacterSpeciesAdapter(context,speciesItems.species)
                        layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                        addItemDecoration(divider)
                    }
                }

                Status.ERROR ->{
                    pbSpeciesLoading.toGone()
                    txtError.toVisible()
                    rcSpecies.toGone()

                }

            }

        }

    }

    class MovieViewHolder(view: View) : BaseViewHolder(view){
        private val txtMovieTitle = view.findViewById<TextView>(R.id.item_movie_txtTitle)
        private val txtOpeningCrawl = view.findViewById<TextView>(R.id.item_movie_txtOpeningCrawl)
        override fun bind(item: CharacterListItem){
            val movieItem = (item as CharacterMovieItem)
            txtMovieTitle.text = movieItem.movieName
            txtOpeningCrawl.text = movieItem.description
        }

    }

    class LoadingErrorMoviesViewHolder(view: View) : BaseViewHolder(view){
        private val txtErrorMessage = view.findViewById<TextView>(R.id.item_character_txtMovieErrorMessage)
        private val pbLoading = view.findViewById<MaterialProgressBar>(R.id.item_character_pbMoviesLoading)
        override fun bind(item: CharacterListItem){
            val mItem = (item as CharacterMovieListErrorItem)
            if (mItem.status == Status.LOADING) {
                pbLoading.toVisible()
                txtErrorMessage.toGone()
            } else {
                pbLoading.toGone()
                txtErrorMessage.toVisible()
                txtErrorMessage.text = item.errorMessage
            }
        }
    }

}