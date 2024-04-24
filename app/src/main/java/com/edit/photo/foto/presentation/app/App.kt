package com.edit.photo.foto.presentation.app

import android.app.Application
import com.edit.photo.foto.presentation.di.AppComponent
import com.edit.photo.foto.presentation.di.AppModule
import com.edit.photo.foto.presentation.di.DaggerAppComponent

class App: Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun getAppComponent(): AppComponent {return appComponent}
}