package com.example.vocabbuddy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vocabbuddy.Dao.SectionDao
import com.example.vocabbuddy.Dao.SynonymsDao
import com.example.vocabbuddy.Dao.WordDao
import com.example.vocabbuddy.Models.Section
import com.example.vocabbuddy.Models.Synonyms
import com.example.vocabbuddy.Models.Word

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {}
}


@Database (
    entities = [Word::class, Section::class, Synonyms::class],
    version = 2
)

abstract class VocabDb : RoomDatabase() {
    abstract fun WordDao() : WordDao
    abstract fun SectionDao() : SectionDao
    abstract fun SynonymsDao() : SynonymsDao
    companion object {
        @Volatile private var instance: VocabDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            VocabDb::class.java, "vocabbuddy.db")
            .createFromAsset("vocabbuddy.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}