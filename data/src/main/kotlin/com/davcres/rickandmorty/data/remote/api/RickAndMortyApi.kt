package com.davcres.rickandmorty.data.remote.api

import com.davcres.rickandmorty.data.remote.model.CharacterDto
import com.davcres.rickandmorty.data.remote.model.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun searchCharacters(
        @Query("name") name: String,
        @Query("page") page: Int = 1,
    ): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int,
    ): CharacterDto
}
