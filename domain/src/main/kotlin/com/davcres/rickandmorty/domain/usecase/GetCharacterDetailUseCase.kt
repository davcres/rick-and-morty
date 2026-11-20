package com.davcres.rickandmorty.domain.usecase

import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository

class GetCharacterDetailUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(id: Int): Result<Character> =
        repository.getCharacterDetail(id)
}
