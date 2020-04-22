package com.example.vocabbuddy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("CREATE TABLE IF NOT EXISTS sections (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `best_score` INTEGER NOT NULL, `progress_status` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}


@Database (
    entities = [Word::class, Section::class],
    version = 2
)

abstract class VocabDb : RoomDatabase() {
    abstract fun WordDao() : WordDao
    abstract fun SectionDao() : SectionDao
    companion object {
        @Volatile private var instance: VocabDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            VocabDb::class.java, "vocabbuddy.db")
            .createFromAsset("vocabbuddy.db")
//            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}