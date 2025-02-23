package com.example.mangashelff.repo

import android.util.Log
import com.example.mangashelff.ApiService
import com.example.mangashelff.MangaItem
import com.example.mangashelff.database.MangaDao
import com.example.mangashelff.database.MangaDataLocal
import javax.inject.Inject


class MangaRepositoryImpl @Inject constructor(private val apiService: ApiService, private val managDao: MangaDao) :
    MangaRepository {


    override suspend fun getMangaData(): List<MangaDataLocal>? {
        var apiResponse:List<MangaItem>?=null
        var response:List<MangaDataLocal>?=null
        try {

            apiResponse  = apiService.getMangaData()
            Log.d("heyy response",apiResponse.toString())
            response= mapAndInsetToDB(apiResponse)
        }
        catch (e:Exception){
            Log.d("heyy Exception",e.toString())
        }
        return response
    }

     fun mapAndInsetToDB(response: List<MangaItem>):List<MangaDataLocal> {

        //if same id data is not there in db, then insert it

        val existingIds = managDao.getAllId().toSet()
        val res=response.filter { it.id !in existingIds }.map { MangaDataLocal(
            id = it.id,
            title = it.title,
            score = it.score,
            category = it.category,
            image = it.image,
            popularity = it.popularity,
            publishedChapterDate = it.publishedChapterDate,
            fav = false,
            read = false
        )}

        Log.d("heyy", res.toString())
         managDao.insertAll(res)
        return res
    }


    override suspend fun loadDataFromDB():List<MangaDataLocal> {
        return managDao.getAllData()
    }

    override suspend fun updateManga(manga: MangaDataLocal) {
        managDao.updateManga(manga)
    }
}