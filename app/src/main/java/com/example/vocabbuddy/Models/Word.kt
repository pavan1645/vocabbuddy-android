package com.example.vocabbuddy.Models

import androidx.room.*

@Entity(tableName = "words",  foreignKeys=[ForeignKey(entity = Section::class, childColumns = ["sect_id"], parentColumns = ["id"])])

data class Word(
    @PrimaryKey val id: Int,
    val word: String,
    val type: String?,
    val definition: String?,
    val phonetic: String?,
    val origin: String?,
    val example: String?,
    @ColumnInfo(defaultValue = "0") val learning_status: Int?,
    val sect_id: Int?
)