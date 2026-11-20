package com.davcres.template.data.remote.api

import com.davcres.template.data.remote.model.CharacterDto
import com.davcres.template.data.remote.model.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun searchCharacters(
        @Query("name") name: String,
    ): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int,
    ): CharacterDto
}
