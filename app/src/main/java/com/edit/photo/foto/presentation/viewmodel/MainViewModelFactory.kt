package com.edit.photo.foto.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.themanufacturers.domain.usecase.DeleteProjectUseCase
import com.themanufacturers.domain.usecase.GetListProjectUseCase
import com.themanufacturers.domain.usecase.GetNewProjectIdUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase

class MainViewModelFactory(
    val getListProjectUseCase: GetListProjectUseCase,
    val getNewProjectIdUseCase: GetNewProjectIdUseCase,
    val getProjectUseCase: GetProjectUseCase,
    val deleteProjectUseCase: DeleteProjectUseCase
 ): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            getListProjectUseCase = getListProjectUseCase,
            getNewProjectIdUseCase = getNewProjectIdUseCase,
            getProjectUseCase = getProjectUseCase,
            deleteProjectUseCase = deleteProjectUseCase
        ) as T
    }
}