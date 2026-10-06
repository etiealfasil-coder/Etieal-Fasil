package com.example.data.repository

import com.example.data.db.EducationalProjectDao
import com.example.data.model.EducationalProject
import kotlinx.coroutines.flow.Flow

class EducationalRepository(
    private val dao: EducationalProjectDao
) {
    val allProjects: Flow<List<EducationalProject>> = dao.getAllProjects()
    val favoriteProjects: Flow<List<EducationalProject>> = dao.getFavoriteProjects()

    fun getProjectById(id: Long): Flow<EducationalProject?> = dao.getProjectById(id)

    fun searchProjects(query: String): Flow<List<EducationalProject>> = dao.searchProjects(query)

    suspend fun insertProject(project: EducationalProject): Long = dao.insertProject(project)

    suspend fun updateProject(project: EducationalProject) = dao.updateProject(project)

    suspend fun deleteProject(project: EducationalProject) = dao.deleteProject(project)

    suspend fun setFavorite(id: Long, isFavorite: Boolean) = dao.setFavorite(id, isFavorite)
}
