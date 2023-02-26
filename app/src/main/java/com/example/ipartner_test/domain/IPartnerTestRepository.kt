package com.example.ipartner_test.domain

import com.example.ipartner_test.domain.models.IPartnerResponse
import retrofit2.Response


interface IPartnerTestRepository {
    suspend fun getAllFertilizers(offset: Int): Response<List<IPartnerResponse>>
    suspend fun findFertilizers(query: String): Response<List<IPartnerResponse>>
    suspend fun getFertilizerById(id: Int): Response<IPartnerResponse>
}