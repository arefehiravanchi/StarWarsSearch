package com.arefe.starwars.ui.characterdetail.model

import com.arefe.starwars.api.Status

data class CharacterPlanetInfo(var name:String, var population:String,var status:Status) : CharacterListItem(TYPE_PLANET_INFO)