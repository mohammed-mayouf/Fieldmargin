package com.mayouf.fieldmargintest.data.network

import com.mayouf.fieldmargintest.data.model.Certificate
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("tech-test/certificates")
    suspend fun getCertificates(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<Certificate>

}