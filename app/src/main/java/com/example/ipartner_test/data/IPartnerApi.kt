package com.example.ipartner_test.data

import com.example.ipartner_test.domain.models.IPartnerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IPartnerApi {
    @GET("/api/ppp/index/")
    suspend fun getAllFertilizers(
        @Query("offset") offset: Int
    ): Response<List<IPartnerResponse>>

    @GET("/api/ppp/index/")
    suspend fun findFertilizers(
        @Query("search") search: String
    ): Response<List<IPartnerResponse>>

    @GET("/api/ppp/item/")
    suspend fun getFertilizerById(
        @Query("id") id: Int
    ): Response<IPartnerResponse>
}