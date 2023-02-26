package com.example.ipartner_test.domain.models

data class IPartnerResponse(
    val categories: Categories,
    val description: String,
    val documentation: String,
    val fields: List<Field>,
    val id: Int,
    val image: String,
    val name: String
) : java.io.Serializable