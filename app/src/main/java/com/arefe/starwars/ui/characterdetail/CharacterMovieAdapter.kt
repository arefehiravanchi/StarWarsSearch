package com.arefe.starwars.ui.characterdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.model.Movie

class CharacterMovieAdapter (context: Context,
                             private val movieList:ArrayList<Movie>) : RecyclerView.Adapter<CharacterMovieAdapter.MovieViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = inflater.inflate(R.layout.item_character_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val txtMovieTitle = view.findViewById<TextView>(R.id.item_movie_txtTitle)
        private val txtOpeningCrawl = view.findViewById<TextView>(R.id.item_movie_txtOpeningCrawl)
        fun bind(movie: Movie){
            txtMovieTitle.text = movie.title
            txtOpeningCrawl.text = movie.opening_crawl
        }


    }
}