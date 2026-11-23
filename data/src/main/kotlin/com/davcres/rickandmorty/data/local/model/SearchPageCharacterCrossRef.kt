package com.davcres.rickandmorty.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "search_page_character_cross_ref",
    primaryKeys = ["query", "page", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = SearchPageEntity::class,
            parentColumns = ["query", "page"],
            childColumns = ["query", "page"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["query", "page"]),
        Index(value = ["characterId"]),
    ],
)
data class SearchPageCharacterCrossRef(
    val query: String,
    val page: Int,
    val characterId: Int,
    val position: Int,
)
