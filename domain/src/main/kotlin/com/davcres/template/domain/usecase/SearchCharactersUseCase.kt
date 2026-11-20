package com.davcres.template.domain.usecase

import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result
import com.davcres.template.domain.repository.CharacterRepository

class SearchCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(query: String): Result<List<Character>> =
        repository.searchCharacters(query)
}
