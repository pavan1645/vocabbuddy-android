package com.example.vocabbuddy.Dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SynonymsDao {

    @Query("SELECT word FROM synonyms WHERE wid = :id")
    suspend fun getSynonyms(id: Int): List<String>

}