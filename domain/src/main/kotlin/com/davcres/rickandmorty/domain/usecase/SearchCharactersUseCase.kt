package com.davcres.rickandmorty.domain.usecase

import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository

class SearchCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(query: String): Result<List<Character>> =
        repository.searchCharacters(query)
}
