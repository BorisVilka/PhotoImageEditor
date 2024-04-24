package com.edit.photo.foto.presentation.di

import com.edit.photo.foto.presentation.activity.EditActivity
import com.edit.photo.foto.presentation.activity.MainActivity
import dagger.Component

@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(editActivity: EditActivity)
}