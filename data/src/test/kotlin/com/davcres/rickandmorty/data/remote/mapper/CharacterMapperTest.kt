package com.davcres.rickandmorty.data.remote.mapper

import com.davcres.rickandmorty.data.remote.model.CharacterDto
import com.davcres.rickandmorty.data.remote.model.LocationDto
import com.davcres.rickandmorty.data.remote.model.OriginDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CharacterMapperTest {

    @Test
    fun `toDomain should correctly map full CharacterDto to Character`() {
        val dto = CharacterDto(
            id = 42,
            name = "Big Mort",
            status = "Alive",
            species = "Alien",
            type = "Parasite",
            gender = "Unknown",
            origin = OriginDto(name = "Gazorpazorp"),
            location = LocationDto(name = "Earth"),
            image = "https://example.com/image.png",
            episode = listOf("ep1", "ep2", "ep3"),
        )

        val domain = dto.toDomain()

        assertEquals(42, domain.id)
        assertEquals("Big Mort", domain.name)
        assertEquals("Alive", domain.status)
        assertEquals("Alien", domain.species)
        assertEquals("Parasite", domain.type)
        assertEquals("Unknown", domain.gender)
        assertEquals("Gazorpazorp", domain.origin)
        assertEquals("Earth", domain.location)
        assertEquals("https://example.com/image.png", domain.imageUrl)
        assertEquals(3, domain.episodeCount)
    }

    @Test
    fun `toDomain should safely handle null fields in CharacterDto`() {
        val dto = CharacterDto(
            id = 1,
            name = "Minimal Character",
            status = null,
            species = null,
            type = null,
            gender = null,
            origin = null,
            location = null,
            image = null,
            episode = emptyList(),
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Minimal Character", domain.name)
        assertEquals("", domain.status)
        assertEquals("", domain.species)
        assertEquals("", domain.type)
        assertEquals("", domain.gender)
        assertEquals("", domain.origin)
        assertEquals("", domain.location)
        assertEquals("", domain.imageUrl)
        assertEquals(0, domain.episodeCount)
    }
}
