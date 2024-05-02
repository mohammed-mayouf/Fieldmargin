package com.mayouf.fieldmargintest

import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.domain.repo.CertificatesRepository
import com.mayouf.fieldmargintest.domain.usecase.GetCertificatesUseCase
import com.mayouf.fieldmargintest.utils.DataState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class GetCertificatesUseCaseTest {

    private lateinit var certificatesRepository: CertificatesRepository
    private lateinit var getCertificatesUseCase: GetCertificatesUseCase

    @Before
    fun setUp() {
        certificatesRepository = mock(CertificatesRepository::class.java)
        getCertificatesUseCase = GetCertificatesUseCase(certificatesRepository)
    }

    @Test
    fun `invoke returns success state from repository`() = runBlockingTest {
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
        `when`(
            certificatesRepository.getAllCertificates(
                10,
                1
            )
        ).thenReturn(flow { emit(DataState.Success(fakeCertificates)) })

        val result = getCertificatesUseCase(10, 1).toList()

        verify(certificatesRepository).getAllCertificates(10, 1)
        assertTrue(result.first() is DataState.Success && (result.first() as DataState.Success).data == fakeCertificates)
    }

    @Test
    fun `invoke returns loading state from repository`() = runBlockingTest {
        `when`(
            certificatesRepository.getAllCertificates(
                10,
                1
            )
        ).thenReturn(flow { emit(DataState.Loading()) })

        val result = getCertificatesUseCase(10, 1).toList()

        verify(certificatesRepository).getAllCertificates(10, 1)
        assert(result.first() is DataState.Loading)
    }

    @Test
    fun `invoke returns error state from repository`() = runBlockingTest {
        val errorMessage = "Error occurred"
        `when`(certificatesRepository.getAllCertificates(10, 1)).thenReturn(flow {
            emit(
                DataState.Error(
                    errorMessage
                )
            )
        })

        val result = getCertificatesUseCase(10, 1).toList()

        verify(certificatesRepository).getAllCertificates(10, 1)
        assertTrue(result.first() is DataState.Error)
        assertEquals(errorMessage, (result.first() as DataState.Error).message)
    }
}
