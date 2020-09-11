package com.arefe.starwars.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arefe.starwars.R
import com.arefe.starwars.ui.characterlist.CharacterListActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }

    private fun initViews(){
        activity_main_lytSearch.setOnClickListener {
            CharacterListActivity.start(this,false)
        }

        activity_main_btnCharacterList.setOnClickListener {
            CharacterListActivity.start(this,true)
        }

    }
}