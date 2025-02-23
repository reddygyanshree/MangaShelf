package com.example.mangashelff.repo

import com.example.mangashelff.database.MangaDataLocal

interface MangaRepository {
    suspend fun getMangaData():List<MangaDataLocal>?
    suspend fun loadDataFromDB():List<MangaDataLocal>
    suspend fun updateManga(manga: MangaDataLocal)

}