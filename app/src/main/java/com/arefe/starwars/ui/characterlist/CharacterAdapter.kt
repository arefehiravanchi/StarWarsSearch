package com.arefe.starwars.ui.characterlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.model.Character

class CharacterAdapter(context: Context, private val characters: ArrayList<Character>,
                       private val onItemClick: (Character) -> Unit) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = inflater.inflate(R.layout.item_character_list, parent, false)
        return CharacterViewHolder(view)
    }

    override fun getItemCount(): Int = characters.size

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lytItem = view.findViewById<LinearLayout>(R.id.item_character_lytItem)
        val txtName = view.findViewById<TextView>(R.id.item_character_txtName)
        val txtBirthYear = view.findViewById<TextView>(R.id.item_character_txtBirthYear)
        val txtInfo = view.findViewById<TextView>(R.id.item_character_txtInfo)

        fun bind(character: Character) {
            txtName.text = character.name
            txtBirthYear.text = itemView.context.getString(R.string.character_birth_date,character.birth_year)
            txtInfo.text = itemView.context.getString(R.string.character_info,character.gender,character.films.size.toString())

            lytItem.setOnClickListener { onItemClick.invoke(character) }

        }

    }
}