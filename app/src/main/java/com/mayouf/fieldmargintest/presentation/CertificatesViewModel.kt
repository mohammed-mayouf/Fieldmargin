package com.mayouf.fieldmargintest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.domain.usecase.GetCertificatesUseCase
import com.mayouf.fieldmargintest.utils.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CertificatesViewModel @Inject constructor(
    private val getCertificatesUseCase: GetCertificatesUseCase
) : ViewModel() {

    private val _certificatesList = MutableStateFlow<DataState<List<Certificate>>>(DataState.Idle())
    val certificatesList: StateFlow<DataState<List<Certificate>>> = _certificatesList

    fun getCertificatesList(limit: Int, page: Int) {
        _certificatesList.value = DataState.Loading()
        viewModelScope.launch {
            getCertificatesUseCase.invoke(limit = limit, page = page).collect {
                _certificatesList.value = it
            }
        }
    }
}