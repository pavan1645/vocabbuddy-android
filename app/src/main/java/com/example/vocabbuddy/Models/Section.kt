package com.example.vocabbuddy.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Entity(tableName = "sections")
data class Section (
    @PrimaryKey val id: Int?,
    @ColumnInfo(defaultValue = "0") val best_score: Int?,
    val name: String,
    @ColumnInfo(defaultValue = "0") val progress_status: Int?
)