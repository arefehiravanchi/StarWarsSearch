package com.arefe.starwars.ui.characterdetail.model

data class CharacterPersonalInfo(val name:String,val birthDate:String,val height:String,val gender:String) :
    CharacterListItem(TYPE_PERSONAL_INFO)