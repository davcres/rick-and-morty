package com.davcres.rickandmorty.data.local.model

import androidx.room.Entity

@Entity(
    tableName = "search_pages",
    primaryKeys = ["query", "page"],
)
data class SearchPageEntity(
    val query: String,
    val page: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val updatedAt: Long,
)
