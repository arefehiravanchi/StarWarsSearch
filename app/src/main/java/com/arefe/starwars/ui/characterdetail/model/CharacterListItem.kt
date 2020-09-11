package com.arefe.starwars.ui.characterdetail.model

open class CharacterListItem(val type:Int) {

    companion object {
        const val TYPE_LIST_TITLE = 1
        const val TYPE_PERSONAL_INFO = 2
        const val TYPE_PLANET_INFO = 3
        const val TYPE_SPECIES_INFO= 4
        const val TYPE_MOVIE = 5
        const val TYPE_MOVIE_LIST_ERROR = 6

    }
}