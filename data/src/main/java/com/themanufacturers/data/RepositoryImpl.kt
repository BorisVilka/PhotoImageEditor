package com.themanufacturers.data

import com.themanufacturers.data.storage.Storage
import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.repository.Repository

class RepositoryImpl(private val storage: Storage): Repository {

    override fun getListProject(): MutableList<ImageProject> {
        return storage.getListProject()
    }

    override fun addProject(project: ImageProject) {
        storage.addProject(project)
    }

    override fun getProject(id: Int): ImageProject {
        return storage.getProject(id)
    }

    override fun projectExist(id: Int): Boolean {
        return storage.projectExist(id)
    }

    override fun modifyProject(project: ImageProject) {
        storage.modifyProject(project)
    }

    override fun getNewProjectId(): Int {
        return storage.getNewProjectId()
    }

    override fun deleteProject(project: ImageProject) {
        storage.deleteProject(project)
    }

}