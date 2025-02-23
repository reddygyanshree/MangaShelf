package com.example.mangashelff

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.mangashelff.database.MangaDao
import com.example.mangashelff.database.MangaDatabase
import com.example.mangashelff.repo.MangaRepository
import com.example.mangashelff.repo.MangaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.jsonkeeper.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMangaRepository(api: ApiService,dao: MangaDao): MangaRepository {
        return MangaRepositoryImpl(api,dao)
    }
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }


    @Provides
    @Singleton
    fun provideDatabase(context: Context): MangaDatabase {
        return Room.databaseBuilder(context, MangaDatabase::class.java, "manga_database")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideUserDao(database: MangaDatabase): MangaDao {
        return database.mangaDao()
    }
}