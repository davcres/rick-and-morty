package com.davcres.rickandmorty.domain.model

data class PagedCharacters(
    val characters: List<Character>,
    val page: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
)
