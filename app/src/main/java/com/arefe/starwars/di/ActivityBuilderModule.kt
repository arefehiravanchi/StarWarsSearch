package com.arefe.starwars.di

import com.arefe.starwars.ui.characterdetail.CharacterDetailActivity
import com.arefe.starwars.ui.characterlist.CharacterListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributePeopleListActivity():CharacterListActivity

    @ContributesAndroidInjector
    abstract fun contributeCharacterDetailsActivity():CharacterDetailActivity

}