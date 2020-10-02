package com.example.vocabbuddy.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabbuddy.Models.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Query("SELECT * FROM words WHERE sect_id=:sectId")
    suspend fun getAllWords(sectId: Int): List<Word>

    @Query("UPDATE words SET learning_status= :status WHERE id = :id")
    fun setLearningStatus(id: Int, status: Int)

}