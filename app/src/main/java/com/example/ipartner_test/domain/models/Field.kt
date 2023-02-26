package com.example.ipartner_test.domain.models

data class Field(
    val flags: Flags,
    val group: Int,
    val image: String,
    val name: String,
    val show: Int,
    val type: String,
    val types_id: Int,
    val value: String
) : java.io.Serializable