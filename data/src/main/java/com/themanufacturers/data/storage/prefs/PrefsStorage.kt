package com.themanufacturers.data.storage.prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.themanufacturers.data.storage.Storage
import com.themanufacturers.domain.model.ImageProject

class PrefsStorage(private val preferences: SharedPreferences): Storage {

    private val gson = Gson()
    private var list = mutableListOf<ImageProject>()

    companion object {
        private const val NEW_PROJECT_ID = "index"
        private const val PROJECT_PREFIX = "project"
    }

    override fun getListProject(): MutableList<ImageProject> {
        list =  preferences.all.toList().filter {
            it.first.startsWith("project")
        }.map { it.second as String}.map { gson.fromJson(it,
            ImageProject::class.java) }.toMutableList()
        return list
    }

    override fun addProject(project: ImageProject) {
        list.add(project)
        var ind = getNewProjectId()
        preferences.edit()
            .putString("$PROJECT_PREFIX${project.id}",gson.toJson(project))
            .putInt(NEW_PROJECT_ID,++ind)
            .apply()
    }

    override fun getProject(id: Int): ImageProject {
        return list.find { it.id == id } ?: gson.fromJson(
            preferences.getString("$PROJECT_PREFIX$id", ""),
            ImageProject::class.java
        )
    }

    override fun projectExist(id: Int): Boolean {
        return preferences.contains("$PROJECT_PREFIX$id")
    }

    override fun modifyProject(project: ImageProject) {
        preferences.edit()
            .putString("$PROJECT_PREFIX${project.id}",gson.toJson(project))
            .apply()
    }

    override fun getNewProjectId(): Int {
        return preferences.getInt(NEW_PROJECT_ID,0)
    }

    override fun deleteProject(project: ImageProject) {
        preferences.edit().remove("$PROJECT_PREFIX${project.id}").apply()
    }
}