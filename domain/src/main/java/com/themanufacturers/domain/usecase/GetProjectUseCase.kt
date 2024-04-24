package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.repository.Repository

class GetProjectUseCase(val repository: Repository) {

    fun execute(id: Int): ImageProject {
       return repository.getProject(id)
    }
}