package com.arefe.starwars.ui.characterdetail.model

import com.arefe.starwars.api.Status
import com.arefe.starwars.api.model.SpeciesWithPlanet

data class CharacterSpeciesInfo(val species:ArrayList<SpeciesWithPlanet>,var status:Status): CharacterListItem(
    TYPE_SPECIES_INFO)