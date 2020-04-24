package com.example.vocabbuddy.Dao

import androidx.room.Dao
import androidx.room.Query
import com.example.vocabbuddy.Models.Section

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections")
    fun getAllSections() : List<Section>
}