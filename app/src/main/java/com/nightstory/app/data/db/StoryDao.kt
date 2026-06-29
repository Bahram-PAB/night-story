package com.nightstory.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id")
    suspend fun getStoryById(id: Long): StoryEntity?

    @Insert
    suspend fun insert(story: StoryEntity): Long

    @Delete
    suspend fun delete(story: StoryEntity)

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}
