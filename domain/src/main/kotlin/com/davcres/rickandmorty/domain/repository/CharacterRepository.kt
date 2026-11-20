package com.davcres.rickandmorty.domain.repository

import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result

interface CharacterRepository {
    suspend fun searchCharacters(query: String): Result<List<Character>>
    suspend fun getCharacterDetail(id: Int): Result<Character>
}
