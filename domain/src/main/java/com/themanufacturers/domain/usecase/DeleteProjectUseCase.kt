package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.repository.Repository

class DeleteProjectUseCase(private val repository: Repository) {

    fun execute(project: ImageProject) {
        repository.deleteProject(project)
    }
}