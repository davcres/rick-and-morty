package com.davcres.rickandmorty.data.cache

import com.davcres.rickandmorty.domain.model.Character
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CharacterMemoryCacheTest {

    private lateinit var timeProvider: TimeProvider
    private lateinit var cache: CharacterMemoryCache
    private val testTtlMillis = 5_000L

    private val sampleCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = "Earth",
            location = "Citadel",
            imageUrl = "url",
            episodeCount = 10,
        )
    )

    @BeforeEach
    fun setUp() {
        timeProvider = mockk()
        cache = CharacterMemoryCacheImpl(timeProvider = timeProvider, ttlMillis = testTtlMillis)
    }

    @Test
    fun `get should return null when key is not in cache`() {
        assertNull(cache.get("Rick"))
    }

    @Test
    fun `put and get should return cached characters within TTL`() {
        every { timeProvider.currentTimeMillis() } returns 1_000L
        cache.put("rick", sampleCharacters)

        every { timeProvider.currentTimeMillis() } returns 2_000L
        val result = cache.get("rick")

        assertNotNull(result)
        assertEquals(sampleCharacters, result)
    }

    @Test
    fun `get should be case-insensitive and trim query`() {
        every { timeProvider.currentTimeMillis() } returns 1_000L
        cache.put("Rick Sanchez", sampleCharacters)

        every { timeProvider.currentTimeMillis() } returns 1_500L
        val result = cache.get("   rick sanchez  ")

        assertNotNull(result)
        assertEquals(sampleCharacters, result)
    }

    @Test
    fun `get should return null and evict entry when TTL has expired`() {
        every { timeProvider.currentTimeMillis() } returns 1_000L
        cache.put("rick", sampleCharacters)

        // Time past TTL (1_000 + 5_001 = 6_001)
        every { timeProvider.currentTimeMillis() } returns 6_001L
        val result = cache.get("rick")

        assertNull(result)

        // Verifying it was evicted
        every { timeProvider.currentTimeMillis() } returns 6_002L
        assertNull(cache.get("rick"))
    }

    @Test
    fun `get should return characters at exact boundary of TTL`() {
        every { timeProvider.currentTimeMillis() } returns 1_000L
        cache.put("rick", sampleCharacters)

        // Exactly at boundary
        every { timeProvider.currentTimeMillis() } returns 6_000L
        val result = cache.get("rick")

        assertNotNull(result)
        assertEquals(sampleCharacters, result)
    }

    @Test
    fun `clear should remove all cached items`() {
        every { timeProvider.currentTimeMillis() } returns 1_000L
        cache.put("rick", sampleCharacters)
        cache.put("morty", sampleCharacters)

        cache.clear()

        assertNull(cache.get("rick"))
        assertNull(cache.get("morty"))
    }
}
