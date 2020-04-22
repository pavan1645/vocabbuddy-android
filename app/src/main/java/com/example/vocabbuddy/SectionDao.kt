package com.example.vocabbuddy

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections")
    fun getAllSections() : List<Section>
}