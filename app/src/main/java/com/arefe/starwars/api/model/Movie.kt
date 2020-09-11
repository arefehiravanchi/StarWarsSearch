package com.arefe.starwars.api.model

data class Movie(
    val title: String, val opening_crawl: String, val director: String,
    val producer: String, val release_date: String
)