package com.davcres.template.data.remote.datasource

import com.davcres.template.data.remote.api.RickAndMortyApi
import com.davcres.template.data.remote.model.CharacterDto
import retrofit2.HttpException

interface CharacterRemoteDataSource {
    suspend fun searchCharacters(name: String): List<CharacterDto>
    suspend fun getCharacter(id: Int): CharacterDto
}

private const val HTTP_NOT_FOUND = 404

class CharacterRemoteDataSourceImpl(
    private val api: RickAndMortyApi,
) : CharacterRemoteDataSource {

    override suspend fun searchCharacters(name: String): List<CharacterDto> =
        try {
            api.searchCharacters(name).results
        } catch (e: HttpException) {
            if (e.code() == HTTP_NOT_FOUND) {
                emptyList()
            } else {
                throw e
            }
        }

    override suspend fun getCharacter(id: Int): CharacterDto =
        api.getCharacter(id)
}
