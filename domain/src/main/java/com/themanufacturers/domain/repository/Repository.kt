package com.themanufacturers.domain.repository

import com.themanufacturers.domain.model.ImageProject

interface Repository {

    fun getListProject(): MutableList<ImageProject>

    fun addProject(project: ImageProject)

    fun getProject(id: Int): ImageProject

    fun projectExist(id: Int): Boolean

    fun modifyProject(project: ImageProject)

    fun getNewProjectId(): Int

    fun deleteProject(project: ImageProject)

}