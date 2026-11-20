package com.davcres.template.domain.repository

import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result

interface CharacterRepository {
    suspend fun searchCharacters(query: String): Result<List<Character>>
    suspend fun getCharacterDetail(id: Int): Result<Character>
}
