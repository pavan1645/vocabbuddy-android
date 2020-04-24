package com.example.vocabbuddy.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.vocabbuddy.Models.Section

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections")
    fun getAllSections() : List<Section>

    @Query("UPDATE sections SET progress_status = '2' WHERE id = '1'")
    fun setSectionProgess()
}