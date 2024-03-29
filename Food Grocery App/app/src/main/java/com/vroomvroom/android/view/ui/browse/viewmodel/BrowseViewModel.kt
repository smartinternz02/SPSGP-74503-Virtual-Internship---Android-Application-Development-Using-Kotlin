package com.himanshu.android.view.ui.browse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.android.domain.db.search.SearchEntity
import com.himanshu.android.repository.local.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    fun insertSearch(searchEntity: SearchEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.insertSearch(searchEntity)
        }
    }

    fun deleteSearch(searchEntity: SearchEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.deleteSearch(searchEntity)
        }
    }

    fun getAllSearch(searches: (List<SearchEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = roomRepository.getAllSearch()
            withContext(Dispatchers.Main) {
                searches.invoke(res)
            }
        }
    }

}