package com.mayouf.fieldmargintest

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mayouf.fieldmargintest.data.model.Certificate
import com.mayouf.fieldmargintest.data.network.ApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getCertificates returns a list of certificates on success`() = runBlocking {
        val expectedResponseData = """
        [{
            "id": "1",
            "originator": "Entity A",
            "originator-country": "Country A",
            "owner": "Owner A",
            "owner-country": "Country B",
            "status": "Active"
          },
          {
            "id": "2",
            "originator": "Entity B",
            "originator-country": "Country C",
            "owner": "Owner B",
            "owner-country": "Country D",
            "status": "Inactive"
          }]
        """

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(expectedResponseData)
        )

        val gson = Gson()
        val typeToken = object : TypeToken<List<Certificate>>() {}.type
        val expectedResponse = gson.fromJson<List<Certificate>>(expectedResponseData, typeToken)

        val result = apiService.getCertificates(10, 1)

        Assert.assertEquals(expectedResponse, result)
    }

    @Test
    fun `getCertificates emits error on network failure`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody("Internal server error")
        )

        try {
            apiService.getCertificates(10, 1)
            Assert.fail("Expected an HttpException to be thrown")
        } catch (e: HttpException) {
            assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, e.code())
        }
    }
}
