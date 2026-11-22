package com.davcres.rickandmorty.data.remote.datasource

import com.davcres.rickandmorty.data.remote.api.RickAndMortyApi
import com.davcres.rickandmorty.data.remote.model.CharacterDto
import com.davcres.rickandmorty.data.remote.model.CharacterResponseDto
import retrofit2.HttpException

interface CharacterRemoteDataSource {
    suspend fun searchCharacters(name: String, page: Int = 1): CharacterResponseDto
    suspend fun getCharacter(id: Int): CharacterDto
}

private const val HTTP_NOT_FOUND = 404

class CharacterRemoteDataSourceImpl(
    private val api: RickAndMortyApi,
) : CharacterRemoteDataSource {

    override suspend fun searchCharacters(name: String, page: Int): CharacterResponseDto =
        try {
            api.searchCharacters(name, page)
        } catch (e: HttpException) {
            // If a page that does not exist is requested, or if the filter returns no results, the API responds with an HTTP 404.
            if (e.code() == HTTP_NOT_FOUND) {
                CharacterResponseDto(info = null, results = emptyList())
            } else {
                throw e
            }
        }

    override suspend fun getCharacter(id: Int): CharacterDto =
        api.getCharacter(id)
}
