package com.mayouf.fieldmargintest.domain

import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CertificatesRepository {
    suspend fun getAllCertificates(limit: Int, page: Int): Flow<DataState<List<Certificate>>>
}