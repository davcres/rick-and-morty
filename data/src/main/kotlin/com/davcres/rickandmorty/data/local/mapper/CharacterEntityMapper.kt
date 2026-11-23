package com.davcres.rickandmorty.data.local.mapper

import com.davcres.rickandmorty.data.local.model.CharacterEntity
import com.davcres.rickandmorty.domain.model.Character

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin,
    location = location,
    imageUrl = imageUrl,
    episodeCount = episodeCount,
)

fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin,
    location = location,
    imageUrl = imageUrl,
    episodeCount = episodeCount,
)
