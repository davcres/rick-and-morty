package com.davcres.rickandmorty.data.remote.mapper

import com.davcres.rickandmorty.data.remote.model.CharacterDto
import com.davcres.rickandmorty.domain.model.Character

fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status.orEmpty(),
    species = species.orEmpty(),
    type = type.orEmpty(),
    gender = gender.orEmpty(),
    origin = origin?.name.orEmpty(),
    location = location?.name.orEmpty(),
    imageUrl = image.orEmpty(),
    episodeCount = episode.size,
)
