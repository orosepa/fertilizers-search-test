package com.example.ipartner_test.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ipartner_test.domain.IPartnerTestRepository
import com.example.ipartner_test.domain.models.IPartnerResponse
import com.example.ipartner_test.util.Constants
import com.example.ipartner_test.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IPartnerTestRepository
) : ViewModel() {

    private val _fertilizers: MutableLiveData<Resource<List<IPartnerResponse>>> = MutableLiveData()
    val fertilizers: LiveData<Resource<List<IPartnerResponse>>> = _fertilizers
    private var fertilizersResponse: MutableList<IPartnerResponse>? = null

    private val _currentFertilizer: MutableLiveData<Resource<IPartnerResponse>> = MutableLiveData()
    val currentFertilizer: LiveData<Resource<IPartnerResponse>> = _currentFertilizer

    private val _isLastPage = MutableLiveData(false)
    val isLastPage: LiveData<Boolean> = _isLastPage

    private var offset = Constants.PAGE_SIZE

    fun getAllFertilizers() = viewModelScope.launch {
        if (offset == Constants.PAGE_SIZE) {
            _fertilizers.postValue(Resource.Loading())
        }
        val response = repository.getAllFertilizers(offset)
        if (response.isSuccessful) {
            offset += Constants.PAGE_SIZE
            Log.d("ViewModel", "Successful loaded! ${response.body()?.size} items")
            response.body()?.let {
                if (it.isEmpty()) {
                    _isLastPage.postValue(true)
                } else {
                    if (fertilizersResponse == null) {
                        fertilizersResponse = it as MutableList<IPartnerResponse>
                    } else {
                        val oldItems = fertilizersResponse
                        val newItems = it
                        oldItems?.addAll(newItems)
                        Log.d("ViewModel", "Offset: $offset")
                    }
                    _fertilizers.postValue(Resource.Success(fertilizersResponse ?: it))
                    _isLastPage.postValue(false)
                }
            }
        } else {
            _fertilizers.postValue(Resource.Error(response.message() ?: "Error loading items"))
        }
    }

    fun searchFertilizers(query: String) = viewModelScope.launch {
        _fertilizers.postValue(Resource.Loading())
        restoreOffset()
        val response = repository.findFertilizers(query)
        if (response.isSuccessful) {
            _fertilizers.postValue(Resource.Success(response.body()!!))
        } else {
            _fertilizers.postValue(Resource.Error(response.message() ?: "Error loading items"))
        }
    }

    fun getFertilizerById(id: Int) = viewModelScope.launch {
        _currentFertilizer.postValue(Resource.Loading())
        restoreOffset()
        val response = repository.getFertilizerById(id)
        if (response.isSuccessful)
            _currentFertilizer.postValue(Resource.Success(response.body()!!))
        else
            _currentFertilizer.postValue(Resource.Error(response.message() ?: "Error loading items"))
    }

    fun restoreOffset() {
        offset = Constants.PAGE_SIZE
        fertilizersResponse = null
    }
}