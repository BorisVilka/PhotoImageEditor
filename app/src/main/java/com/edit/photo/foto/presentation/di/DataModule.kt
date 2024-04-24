package com.edit.photo.foto.presentation.di

import android.content.Context
import android.content.SharedPreferences
import com.themanufacturers.data.storage.prefs.PrefsStorage
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providePreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("prefs",Context.MODE_PRIVATE)
    }

    @Provides
    fun provideStorage(preferences: SharedPreferences): com.themanufacturers.data.storage.Storage {
        return PrefsStorage(preferences)
    }

    @Provides
    fun provideRepository(storage: com.themanufacturers.data.storage.Storage): com.themanufacturers.domain.repository.Repository {
        return com.themanufacturers.data.RepositoryImpl(storage)
    }
}