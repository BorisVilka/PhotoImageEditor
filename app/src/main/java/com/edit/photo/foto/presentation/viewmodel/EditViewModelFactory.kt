package com.edit.photo.foto.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.themanufacturers.domain.usecase.AddProjectUseCase
import com.themanufacturers.domain.usecase.GetListProjectUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase
import com.themanufacturers.domain.usecase.ModifyProjectUseCase
import com.themanufacturers.domain.usecase.ProjectExistUseCase

class EditViewModelFactory(
    val getProjectUseCase: GetProjectUseCase,
    val addProjectUseCase: AddProjectUseCase,
    val projectExistUseCase: ProjectExistUseCase,
    val modifyProjectUseCase: ModifyProjectUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditViewModel(
            getProjectUseCase = getProjectUseCase,
            addProjectUseCase = addProjectUseCase,
            projectExistUseCase = projectExistUseCase,
            modifyProjectUseCase = modifyProjectUseCase
        ) as T
    }
}