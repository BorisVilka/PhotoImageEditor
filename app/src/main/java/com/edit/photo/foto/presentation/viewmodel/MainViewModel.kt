package com.edit.photo.foto.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.usecase.DeleteProjectUseCase
import com.themanufacturers.domain.usecase.GetListProjectUseCase
import com.themanufacturers.domain.usecase.GetNewProjectIdUseCase
import com.themanufacturers.domain.usecase.GetProjectUseCase

class MainViewModel(
    val getListProjectUseCase: GetListProjectUseCase,
    val getNewProjectIdUseCase: GetNewProjectIdUseCase,
    val getProjectUseCase: GetProjectUseCase,
    val deleteProjectUseCase: DeleteProjectUseCase
): ViewModel() {

    fun getList(): MutableList<ImageProject> {
        return getListProjectUseCase.execute()
    }

    fun getNewProjectId(): Int {
        return getNewProjectIdUseCase.execute()
    }

    fun getProject(id: Int): ImageProject {
        return getProjectUseCase.execute(id)
    }

    fun deleteProject(project: ImageProject) {
        deleteProjectUseCase.execute(project)
    }

}