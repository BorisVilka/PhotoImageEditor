package com.edit.photo.foto.presentation.di

import android.content.Context
import com.themanufacturers.domain.usecase.AddProjectUseCase
import com.themanufacturers.domain.usecase.GetListProjectUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase
import com.themanufacturers.domain.usecase.ModifyProjectUseCase
import com.themanufacturers.domain.usecase.ProjectExistUseCase
import com.edit.photo.foto.presentation.viewmodel.EditViewModelFactory
import com.edit.photo.foto.presentation.viewmodel.MainViewModelFactory
import com.themanufacturers.domain.usecase.DeleteProjectUseCase
import com.themanufacturers.domain.usecase.GetNewProjectIdUseCase
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideMainActivityViewModelFactory(
        getListProjectUseCase: GetListProjectUseCase,
        getNewProjectIdUseCase: GetNewProjectIdUseCase,
        getProjectUseCase: GetProjectUseCase,
        deleteProjectUseCase: DeleteProjectUseCase
     ): MainViewModelFactory {
        return  MainViewModelFactory(
            getListProjectUseCase = getListProjectUseCase,
            getNewProjectIdUseCase = getNewProjectIdUseCase,
            getProjectUseCase = getProjectUseCase,
            deleteProjectUseCase = deleteProjectUseCase
          )
    }

    @Provides
    fun provideEditActivityViewModelFactory(
        getProjectUseCase: GetProjectUseCase,
        addProjectUseCase: AddProjectUseCase,
        projectExistUseCase: ProjectExistUseCase,
        modifyProjectUseCase: ModifyProjectUseCase
    ): EditViewModelFactory {
        return  EditViewModelFactory(
            getProjectUseCase = getProjectUseCase,
            addProjectUseCase = addProjectUseCase,
            projectExistUseCase = projectExistUseCase,
            modifyProjectUseCase = modifyProjectUseCase
        )
    }
}