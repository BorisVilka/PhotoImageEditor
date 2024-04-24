package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.repository.Repository

class GetListProjectUseCase(val repository: Repository) {

    fun execute():MutableList<ImageProject> {
        return repository.getListProject()
    }
}