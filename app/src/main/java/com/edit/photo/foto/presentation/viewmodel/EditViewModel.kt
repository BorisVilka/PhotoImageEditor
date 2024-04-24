package com.edit.photo.foto.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.usecase.AddProjectUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase
import com.themanufacturers.domain.usecase.ModifyProjectUseCase
import com.themanufacturers.domain.usecase.ProjectExistUseCase

class EditViewModel(
    val getProjectUseCase: GetProjectUseCase,
    val addProjectUseCase: AddProjectUseCase,
    val projectExistUseCase: ProjectExistUseCase,
    val modifyProjectUseCase: ModifyProjectUseCase
): ViewModel() {

    fun addProject(project: ImageProject) {
        addProjectUseCase.execute(project)
    }

    fun getItemById(id: Int): ImageProject {
        return getProjectUseCase.execute(id)
    }

    fun projectExist(id:Int): Boolean {
        return projectExistUseCase.execute(id)
    }
    fun modifyProject(project: ImageProject) {
        modifyProjectUseCase.execute(project)
    }
}