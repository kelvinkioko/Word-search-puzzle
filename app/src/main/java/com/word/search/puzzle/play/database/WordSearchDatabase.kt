package com.word.search.puzzle.play.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.word.search.puzzle.play.constants.Converters
import com.word.search.puzzle.play.database.entity.PlaysEntity

@Database(entities = [PlaysEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class WordSearchDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: WordSearchDatabase? = null

        fun getDatabase(context: Context): WordSearchDatabase {
            var tempInstance = INSTANCE
            if (tempInstance == null) {
                tempInstance = Room.databaseBuilder(context.applicationContext,
                    WordSearchDatabase::class.java, "wsp_schema")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return tempInstance
        }
    }
}
