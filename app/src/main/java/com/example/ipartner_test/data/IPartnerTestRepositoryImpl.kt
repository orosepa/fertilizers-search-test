package com.example.ipartner_test.data

import com.example.ipartner_test.domain.IPartnerTestRepository

class IPartnerTestRepositoryImpl(
    private val api: IPartnerApi
) : IPartnerTestRepository {

    override suspend fun getAllFertilizers(offset: Int) = api.getAllFertilizers(offset)
    override suspend fun findFertilizers(query: String) = api.findFertilizers(query)
    override suspend fun getFertilizerById(id: Int) = api.getFertilizerById(id)
}