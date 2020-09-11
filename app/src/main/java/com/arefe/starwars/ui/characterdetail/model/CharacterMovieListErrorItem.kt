package com.arefe.starwars.ui.characterdetail.model

import com.arefe.starwars.api.Status

data class CharacterMovieListErrorItem(var errorMessage: String,var status: Status):CharacterListItem(
    TYPE_MOVIE_LIST_ERROR)