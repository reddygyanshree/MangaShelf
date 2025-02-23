package com.example.mangashelff.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "manga_table")
data class MangaDataLocal(
    @PrimaryKey val id: String,
    val title: String,
    val score: Double,
    val popularity: Int,
    val publishedChapterDate: Long,
    val image: String,
    val category: String,
    var fav : Boolean=false,
    var read : Boolean=false
)

