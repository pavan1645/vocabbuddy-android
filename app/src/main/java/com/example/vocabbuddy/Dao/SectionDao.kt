package com.example.vocabbuddy.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.vocabbuddy.Models.Section

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections")
    fun getAllSections() : List<Section>

    @Query("SELECT * FROM sections where id = :id")
    fun getSectionById(id: Int): Section

    @Query("UPDATE sections SET progress_status = :status WHERE id = :id")
    fun setSectionProgess(id: Int, status: Int)

    @Query("UPDATE sections SET best_score = :bestScore WHERE id = :id")
    fun setSectionScore(id: Int, bestScore: Int)
}