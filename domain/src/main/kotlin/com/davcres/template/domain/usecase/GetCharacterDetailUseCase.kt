package com.davcres.template.domain.usecase

import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result
import com.davcres.template.domain.repository.CharacterRepository

class GetCharacterDetailUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(id: Int): Result<Character> =
        repository.getCharacterDetail(id)
}
