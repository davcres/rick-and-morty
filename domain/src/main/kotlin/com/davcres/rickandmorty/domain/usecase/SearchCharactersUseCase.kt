package com.davcres.rickandmorty.domain.usecase

import com.davcres.rickandmorty.domain.model.PagedCharacters
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository

class SearchCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<PagedCharacters> =
        repository.searchCharacters(query, page)
}
