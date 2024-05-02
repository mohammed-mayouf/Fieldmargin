package com.mayouf.fieldmargintest.data.repo

import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.data.network.ApiService
import com.mayouf.fieldmargintest.utils.DataState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class CertificatesRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: CertificatesRepositoryImpl

    @Before
    fun setUp() {
        apiService = mock(ApiService::class.java)
        repository = CertificatesRepositoryImpl(
            apiService,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun `getAllCertificates emits DataState_Success on successful fetch`() = runBlockingTest {
        val fakeCertificates = listOf(
            Certificate(
                id = "1",
                originator = "Originator A",
                originatorCountry = "Country A",
                owner = "Owner A",
                ownerCountry = "Country B",
                status = "Active"
            )
        )
        `when`(apiService.getCertificates(10, 1)).thenReturn(fakeCertificates)

        val results = repository.getAllCertificates(10, 1).toList()

        assert(results.first() is DataState.Loading)
        assert(results[1] is DataState.Success)
        assertEquals(fakeCertificates, (results[1] as DataState.Success).data)
    }

    @Test
    fun `getAllCertificates emits DataState_Error on HTTP 404 response`() = runBlockingTest {
        val httpException =
            HttpException(Response.error<Any>(404, ResponseBody.create(null, "Not Found")))
        `when`(apiService.getCertificates(10, 1)).thenThrow(httpException)

        val results = repository.getAllCertificates(10, 1).toList()

        assert(results.first() is DataState.Loading)
        assert(results[1] is DataState.Error)
        assert((results[1] as DataState.Error).message!!.contains("Not Found"))
    }

    @Test
    fun `getAllCertificates emits DataState_Error on timeout exception`() = runBlockingTest {
        `when`(apiService.getCertificates(10, 1)).thenThrow(RuntimeException("Timeout"))

        val results = repository.getAllCertificates(10, 1).toList()

        assert(results.first() is DataState.Loading)
        assert(results[1] is DataState.Error)
        assertEquals("Timeout", (results[1] as DataState.Error).message)
    }

    @Test
    fun `getAllCertificates emits DataState_Error on network error`() = runBlockingTest {
        doAnswer { throw IOException("Network Error. Please check your internet connection.") }.`when`(
            apiService
        ).getCertificates(10, 1)

        val results = repository.getAllCertificates(10, 1).toList()

        assert(results.first() is DataState.Loading)
        assert(results[1] is DataState.Error)
        assertEquals(
            "Network Error. Please check your internet connection.",
            (results[1] as DataState.Error).message
        )
    }

    @Test
    fun `getAllCertificates emits DataState_Error on unexpected exception`() = runBlockingTest {
        doAnswer { throw Exception("Unexpected Error") }.`when`(apiService).getCertificates(10, 1)

        val results = repository.getAllCertificates(10, 1).toList()

        assert(results.first() is DataState.Loading)
        assert(results[1] is DataState.Error)
        assertEquals("Unexpected Error", (results[1] as DataState.Error).message)
    }
}
