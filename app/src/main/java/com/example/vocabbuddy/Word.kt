package com.example.vocabbuddy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey val id: Int,
    val word: String
)