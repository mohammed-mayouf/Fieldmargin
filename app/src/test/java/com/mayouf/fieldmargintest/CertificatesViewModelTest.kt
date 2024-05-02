package com.mayouf.fieldmargintest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.data.model.UiCertificate
import com.mayouf.fieldmargintest.domain.usecase.GetCertificatesUseCase
import com.mayouf.fieldmargintest.presentation.viewmodel.CertificatesViewModel
import com.mayouf.fieldmargintest.utils.DataState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class CertificatesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var getCertificatesUseCase: GetCertificatesUseCase

    private lateinit var viewModel: CertificatesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        runTest {
            // Preparing a flow of DataState with List<Certificate>
            val fakeCertificatesFlow = flowOf(
                DataState.Loading(),
                DataState.Success(
                    listOf(
                        Certificate(
                            id = "1",
                            originator = "Originator A",
                            originatorCountry = "Country A",
                            owner = "Owner A",
                            ownerCountry = "Country B",
                            status = "Active"
                        )
                    )
                )
            )
            `when`(getCertificatesUseCase.invoke(10, 1)).thenReturn(fakeCertificatesFlow)
        }
        viewModel = CertificatesViewModel(getCertificatesUseCase)
    }


    @Test
    fun `certificatesList updates to loading then success state`() = runTest {
        viewModel.getCertificatesList(10, 1)

        val certificatesList = viewModel.certificatesList.take(2).toList()

        assertTrue(certificatesList[0] is DataState.Loading)
        assertTrue(certificatesList[1] is DataState.Success)
        assertEquals(
            "Owner A",
            (certificatesList[1] as DataState.Success).data!!.first().certificate.owner
        )
    }

    @Test
    fun `certificatesList updates to error state when use case returns error`() = runTest {
        val errorMessage = "Error fetching certificates"
        val errorFlow = flowOf(DataState.Error<List<Certificate>>(errorMessage))
        `when`(getCertificatesUseCase.invoke(10, 1)).thenReturn(errorFlow)

        viewModel.getCertificatesList(10, 1)

        val certificatesList = viewModel.certificatesList.take(2).toList()

        assertTrue(certificatesList[0] is DataState.Loading)
        assertTrue(certificatesList[1] is DataState.Error)
        assertEquals(errorMessage, (certificatesList[1] as DataState.Error).message)
    }

    @Test
    fun `certificatesList remains idle when use case returns no certificates`() = runTest {
        val emptyFlow = flowOf(DataState.Success<List<Certificate>>(emptyList()))
        `when`(getCertificatesUseCase.invoke(10, 1)).thenReturn(emptyFlow)

        viewModel.getCertificatesList(10, 1)

        val certificatesList = viewModel.certificatesList.take(2).toList()

        assertTrue(certificatesList[0] is DataState.Loading)
        assertTrue(certificatesList[1] is DataState.Success)
        assertTrue((certificatesList[1] as DataState.Success).data!!.isEmpty())
    }

    @Test
    fun `certificatesList handles exceptions thrown by use case`() = runTest {
        `when`(getCertificatesUseCase.invoke(10, 1)).thenReturn(
            flowOf(
                DataState.Loading(),
                DataState.Error("Test exception")
            )
        )
        viewModel.getCertificatesList(10, 1)
        val states = viewModel.certificatesList.take(2).toList()

        assertTrue(states[0] is DataState.Loading)
        assertTrue(states[1] is DataState.Error)
        assertEquals("Test exception", (states[1] as DataState.Error).message)
    }

    @Test
    fun `toggleFavorite updates favorite status of a certificate`() = runTest {
        // Initially, all items are not favorite
        val initialCertificates = listOf(
            Certificate("1", "Originator A", "Country A", "Owner A", "Country B", "Active"),
            Certificate("2", "Originator B", "Country B", "Owner B", "Country C", "Inactive")
        )
        val uiCertificates = initialCertificates.map { UiCertificate(it) }
        val initialStates = viewModel.certificatesList.first()
        assertTrue(initialStates is DataState.Success)
        val initialFavoriteStatus = (initialStates as DataState.Success).data!![0].isFavorite

        // Toggle favorite status of the first certificate
        viewModel.toggleFavorite("1")

        // Check that the favorite status is updated
        val updatedStates = viewModel.certificatesList.first()
        assertTrue(updatedStates is DataState.Success)
        val updatedFavoriteStatus = (updatedStates as DataState.Success).data!![0].isFavorite
        assertEquals(!initialFavoriteStatus, updatedFavoriteStatus)

        // Verify the state has been updated properly
        verify(getCertificatesUseCase).invoke(10, 1)
    }
}
