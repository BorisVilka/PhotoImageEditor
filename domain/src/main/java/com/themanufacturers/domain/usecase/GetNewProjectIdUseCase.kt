package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.repository.Repository

class GetNewProjectIdUseCase(val repository: Repository) {

    fun execute(): Int {
        return repository.getNewProjectId()
    }
}