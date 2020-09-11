package com.arefe.starwars.api.model

data class Species(val name:String,val homeworld:String?,val language:String)

data class SpeciesWithPlanet(val species:Species,val planet: Planet?)
