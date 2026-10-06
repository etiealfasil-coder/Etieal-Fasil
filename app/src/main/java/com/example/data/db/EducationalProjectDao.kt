package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EducationalProject
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationalProjectDao {
    @Query("SELECT * FROM educational_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<EducationalProject>>

    @Query("SELECT * FROM educational_projects WHERE id = :id")
    fun getProjectById(id: Long): Flow<EducationalProject?>

    @Query("SELECT * FROM educational_projects WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteProjects(): Flow<List<EducationalProject>>

    @Query("SELECT * FROM educational_projects WHERE titleEnglish LIKE '%' || :query || '%' OR titleAmharic LIKE '%' || :query || '%' OR topicCategory LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<EducationalProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: EducationalProject): Long

    @Update
    suspend fun updateProject(project: EducationalProject)

    @Delete
    suspend fun deleteProject(project: EducationalProject)

    @Query("UPDATE educational_projects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM educational_projects")
    suspend fun getProjectCount(): Int
}
