package com.mayouf.fieldmargintest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayouf.fieldmargintest.data.model.UiCertificate
import com.mayouf.fieldmargintest.domain.usecase.GetCertificatesUseCase
import com.mayouf.fieldmargintest.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertificatesViewModel @Inject constructor(
    private val getCertificatesUseCase: GetCertificatesUseCase
) : ViewModel() {

    private val _certificatesList =
        MutableStateFlow<DataState<List<UiCertificate>>>(DataState.Idle())
    val certificatesList: StateFlow<DataState<List<UiCertificate>>> = _certificatesList

    val favoritesList: StateFlow<List<UiCertificate>> = _certificatesList.map { state ->
        when (state) {
            is DataState.Success -> state.data!!.filter { it.isFavorite }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getCertificatesList(10, 1)
    }

    fun getCertificatesList(limit: Int, page: Int) {
        _certificatesList.value = DataState.Loading()
        viewModelScope.launch {
            getCertificatesUseCase.invoke(limit = limit, page = page).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        val uiCertificates = dataState.data!!.map { certificate ->
                            UiCertificate(certificate = certificate)
                        }
                        _certificatesList.value = DataState.Success(uiCertificates)
                    }

                    is DataState.Error -> _certificatesList.value =
                        DataState.Error(dataState.message!!)

                    is DataState.Loading -> _certificatesList.value = DataState.Loading()
                    else -> _certificatesList.value = DataState.Idle()
                }
            }
        }
    }

    fun toggleFavorite(certificateId: String) {
        _certificatesList.value.let { currentState ->
            if (currentState is DataState.Success) {
                val updatedCertificates = currentState.data!!.map { uiCertificate ->
                    if (uiCertificate.certificate.id == certificateId)
                        uiCertificate.copy(isFavorite = !uiCertificate.isFavorite)
                    else
                        uiCertificate
                }
                _certificatesList.value = DataState.Success(updatedCertificates)
            }
        }
    }
}