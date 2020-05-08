package com.example.vocabbuddy.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "synonyms",  foreignKeys=[ForeignKey(entity = Word::class, childColumns = ["wid"], parentColumns = ["id"])])
data class Synonyms (
    @PrimaryKey val id: Int,
    val wid: Int,
    val word: String
)