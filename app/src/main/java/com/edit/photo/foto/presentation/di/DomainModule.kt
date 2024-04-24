package com.edit.photo.foto.presentation.di

import com.themanufacturers.domain.repository.Repository
import com.themanufacturers.domain.usecase.AddProjectUseCase
import com.themanufacturers.domain.usecase.DeleteProjectUseCase
import com.themanufacturers.domain.usecase.GetListProjectUseCase
import com.themanufacturers.domain.usecase.GetNewProjectIdUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase
import com.themanufacturers.domain.usecase.ModifyProjectUseCase
import com.themanufacturers.domain.usecase.ProjectExistUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideAddProjectUseCase(repository: Repository): AddProjectUseCase {
        return AddProjectUseCase(repository)
    }

    @Provides
    fun provideGetListProjectUseCase(repository: Repository): GetListProjectUseCase {
        return GetListProjectUseCase(repository)
    }

    @Provides
    fun provideGetProjectUseCase(repository: Repository): GetProjectUseCase {
        return GetProjectUseCase(repository)
    }

    @Provides
    fun provideModifyProjectUseCase(repository: Repository): ModifyProjectUseCase {
        return ModifyProjectUseCase(repository)
    }

    @Provides
    fun provideProjectExistUseCase(repository: Repository): ProjectExistUseCase {
        return ProjectExistUseCase(repository)
    }

    @Provides
    fun provideGetNewProjectIdUseCase(repository: Repository): GetNewProjectIdUseCase {
        return GetNewProjectIdUseCase(repository)
    }

    @Provides
    fun provideDeleteProjectUseCase(repository: Repository): DeleteProjectUseCase {
        return DeleteProjectUseCase(repository)
    }
}