package com.example.mangashelff

import retrofit2.http.GET

interface ApiService {

    @GET("b/KEJO")
    suspend fun getMangaData():List<MangaItem>
}