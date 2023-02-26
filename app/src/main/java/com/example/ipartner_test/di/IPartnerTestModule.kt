package com.example.ipartner_test.di

import com.example.ipartner_test.data.IPartnerApi
import com.example.ipartner_test.data.IPartnerTestRepositoryImpl
import com.example.ipartner_test.domain.IPartnerTestRepository
import com.example.ipartner_test.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IPartnerTestModule {

    @Singleton
    @Provides
    fun provideIPartnerApi(): IPartnerApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IPartnerApi::class.java)
    }

    @Singleton
    @Provides
    fun provideIPartnerTestRepository(api: IPartnerApi): IPartnerTestRepository =
        IPartnerTestRepositoryImpl(api)
}