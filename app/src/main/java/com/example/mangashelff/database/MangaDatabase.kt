package com.example.mangashelff.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MangaDataLocal::class], version = 1, exportSchema = false)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    companion object {
        @Volatile
        private var INSTANCE: MangaDatabase? = null

        fun getDatabase(context: Context): MangaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MangaDatabase::class.java,
                    "manga_Database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}