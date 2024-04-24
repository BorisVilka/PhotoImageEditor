package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.repository.Repository

class ModifyProjectUseCase(val repository: Repository) {

    fun execute(project: ImageProject) {
        repository.modifyProject(project)
    }

}