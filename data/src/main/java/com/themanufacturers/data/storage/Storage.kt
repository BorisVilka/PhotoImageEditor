package com.themanufacturers.data.storage

import com.themanufacturers.domain.model.ImageProject


interface Storage {

    fun getListProject(): MutableList<ImageProject>

    fun addProject(project: ImageProject)

    fun getProject(id: Int): ImageProject

    fun projectExist(id: Int): Boolean

    fun modifyProject(project: ImageProject)

    fun getNewProjectId(): Int

    fun deleteProject(project: ImageProject)
}