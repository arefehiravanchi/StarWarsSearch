package com.arefe.starwars.ui.characterdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.model.SpeciesWithPlanet

class CharacterSpeciesAdapter (context: Context,
                               private val speciesList:ArrayList<SpeciesWithPlanet>) : RecyclerView.Adapter<CharacterSpeciesAdapter.SpeciesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val view = inflater.inflate(R.layout.item_species, parent, false)
        return SpeciesViewHolder(view)
    }

    override fun getItemCount(): Int = speciesList.size

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        holder.bind(speciesList[position])
    }

    class SpeciesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val txtSpecieName = view.findViewById<TextView>(R.id.item_species_txtName)
        private val txtLanguage = view.findViewById<TextView>(R.id.item_species_txtLanguage)
        private val txtPlanetName = view.findViewById<TextView>(R.id.item_species_txtHomeWorld)
        fun bind(speciesWithPlanet: SpeciesWithPlanet){
            txtSpecieName.text = itemView.context.getString(R.string.formatted_name,speciesWithPlanet.species.name)
            txtLanguage.text = itemView.context.getString(R.string.formatted_language,speciesWithPlanet.species.language)
            speciesWithPlanet.planet?.name?.let {
                txtPlanetName.text = itemView.context.getString(R.string.formatted_homeworld,it)
            }

        }

    }
}