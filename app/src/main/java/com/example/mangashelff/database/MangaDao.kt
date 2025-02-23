package com.example.mangashelff.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(mangaList: List<MangaDataLocal>)

    @Query("SELECT * FROM manga_table")
    fun getAllData(): List<MangaDataLocal>

    @Query("SELECT id FROM manga_table")
    fun getAllId(): List<String>

//    @Update
//    suspend fun updateAll(mangaList: List<MangaDataLocal>)

//    @Query("SELECT * FROM manga_table WHERE id = :id ")
//    suspend fun getMangaById(id: String): MangaDataLocal?

    @Update
    fun updateManga(manga: MangaDataLocal)
}