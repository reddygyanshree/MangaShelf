package com.example.mangashelff

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelff.database.MangaDataLocal
import com.example.mangashelff.repo.MangaRepository
import com.example.mangashelff.ui.common.Constants
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MangaViewModel @Inject constructor(private val repository: MangaRepository,private val context: Context) : ViewModel() {
    private val _mangaData = MutableStateFlow<List<MangaDataLocal>>(emptyList())
    val mangaData: StateFlow<List<MangaDataLocal>> = _mangaData


    var selectedManga= mutableStateOf<MangaDataLocal?>(null)

    private val connectivityObserver = ConnectivityObserver(context.applicationContext)
    val isConnected: StateFlow<Boolean> = connectivityObserver.isConnected

    private val _showBackOnlineMessage = MutableStateFlow(false)
    val showBackOnlineMessage: StateFlow<Boolean> = _showBackOnlineMessage
    private var wasOffline = false
    val showYearBar =  mutableStateOf(true)

    init {
        observeInternetReconnect()
    }

    private fun observeInternetReconnect() {
        viewModelScope.launch {
            isConnected.collect { connected ->
                if (!connected) {
                    wasOffline = true // Mark as offline
                } else if (wasOffline) {
                    // Only show "Back Online" if we were offline before
                    _showBackOnlineMessage.value = true
                    wasOffline = false // Reset flag after showing message
                    delay(2000) // Show message for 2 seconds
                    _showBackOnlineMessage.value = false
                    loadData()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityObserver.stopObserving()
    }



    //data through api
    fun getMangaDataFRomApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getMangaData()
                if (data != null) {
                    Log.d("heyy posting data 28",data.toString())
                    _mangaData.value = mangaData.value + data
                }
            } catch (e: Exception) {
                Log.d("Exception",e.toString())
            }
        }
    }

     suspend fun loadData() {
        getMangaDataFRomApi()
        loadDataFRomDB()
    }

    suspend fun loadDataFRomDB(){
        viewModelScope.launch(Dispatchers.IO){
            _mangaData.value = repository.loadDataFromDB()
            Log.d("heyy posting data 42",_mangaData.value.toString())
        }
    }


    fun updateManga(manga: MangaDataLocal){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateManga(manga)
        }
    }
    fun setSelectedOption(option: String) {
        _mangaData.value = when (option) {
            Constants.SCORE_ASC -> _mangaData.value.sortedBy { it.score }
            Constants.SCORE_DESC -> _mangaData.value.sortedByDescending { it.score }
            Constants.POPULARITY_ASC -> _mangaData.value.sortedBy { it.popularity }
            Constants.POPULARITY_DESC -> _mangaData.value.sortedByDescending { it.popularity }
            else -> { _mangaData.value }
        }
    }

    fun setFilteredList(options: List<String>) {
        if (options.isEmpty()){
            showYearBar.value=true
            return
        }
        if (options.contains("ALL")){
            showYearBar.value=true
            return
        }
        _mangaData.value = _mangaData.value.filter { manga ->
            options.contains(manga.category)
        }
    }

    fun refreshAndFilter(options: List<String>) {
        viewModelScope.launch (Dispatchers.IO){
            loadData() // Load fresh data
            kotlinx.coroutines.delay(100)
            withContext(Dispatchers.Main) {
                setFilteredList(options)
            }
        }
    }

}