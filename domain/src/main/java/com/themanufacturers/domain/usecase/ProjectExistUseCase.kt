package com.themanufacturers.domain.usecase

import com.themanufacturers.domain.repository.Repository

class ProjectExistUseCase(val repository: Repository) {

    fun execute(id:Int): Boolean {
        return repository.projectExist(id)
    }

}