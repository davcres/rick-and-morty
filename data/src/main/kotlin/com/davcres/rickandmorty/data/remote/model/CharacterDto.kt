package com.davcres.rickandmorty.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponseDto(
    val info: PageInfoDto? = null,
    val results: List<CharacterDto> = emptyList(),
)

@Serializable
data class PageInfoDto(
    val count: Int? = null,
    val pages: Int? = null,
    val next: String? = null,
    val prev: String? = null,
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
    val gender: String? = null,
    val origin: OriginDto? = null,
    val location: LocationDto? = null,
    val image: String? = null,
    val episode: List<String> = emptyList(),
    val url: String? = null,
    val created: String? = null,
)

@Serializable
data class OriginDto(
    val name: String? = null,
    val url: String? = null,
)

@Serializable
data class LocationDto(
    val name: String? = null,
    val url: String? = null,
)
