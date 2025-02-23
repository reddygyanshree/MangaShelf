package com.example.mangashelff

data class MangaItem(
    val category: String,
    val id: String,
    val image: String,
    val popularity: Int,
    val publishedChapterDate: Long,
    val score: Double,
    val title: String
)
