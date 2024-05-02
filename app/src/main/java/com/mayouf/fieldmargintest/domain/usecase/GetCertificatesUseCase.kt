package com.mayouf.fieldmargintest.domain.usecase

import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.domain.repo.CertificatesRepository
import com.mayouf.fieldmargintest.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCertificatesUseCase @Inject constructor(private val repository: CertificatesRepository) {

    suspend operator fun invoke(limit: Int, page: Int): Flow<DataState<List<Certificate>>> {
        return repository.getAllCertificates(limit = limit, page = page)
    }
}