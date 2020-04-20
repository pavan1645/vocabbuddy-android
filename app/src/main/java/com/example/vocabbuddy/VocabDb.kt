package com.example.vocabbuddy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (
    entities = [Word::class],
    version = 1
)

abstract class VocabDb : RoomDatabase() {
    abstract fun WordDao() : WordDao

    companion object {
        @Volatile private var instance: VocabDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            VocabDb::class.java, "vocabbuddy.db")
            .createFromAsset("vocabbuddy.db")
            .build()
    }
}