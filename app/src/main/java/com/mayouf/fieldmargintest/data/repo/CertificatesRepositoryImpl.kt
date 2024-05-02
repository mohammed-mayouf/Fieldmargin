package com.mayouf.fieldmargintest.data.repo

import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.data.network.ApiService
import com.mayouf.fieldmargintest.domain.CertificatesRepository
import com.mayouf.fieldmargintest.utils.DataState
import com.mayouf.fieldmargintest.utils.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CertificatesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
) : CertificatesRepository {
    override suspend fun getAllCertificates(
        limit: Int,
        page: Int
    ): Flow<DataState<List<Certificate>>> {
        return flow {
            emit(DataState.Loading())
            val response = safeApiCall { apiService.getCertificates(limit, page) }
            emit(response)
        }.catch { exception ->
            emit(DataState.Error(exception.localizedMessage ?: "Unknown error occurred"))
        }.flowOn(ioDispatcher)
    }
}