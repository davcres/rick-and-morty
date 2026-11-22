package com.davcres.rickandmorty.feature.characters

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.search.components.CharacterCard
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharacterCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episodeCount = 51,
    )

    @Test
    fun characterCard_displaysNameAndDetails() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterCard(
                    character = sampleCharacter,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alive - Human").assertIsDisplayed()
    }

    @Test
    fun characterCard_clickTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterCard(
                    character = sampleCharacter,
                    onClick = { clicked = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Rick Sanchez").performClick()
        assertTrue(clicked)
    }
}

