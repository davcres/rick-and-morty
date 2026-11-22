package com.davcres.rickandmorty.feature.characters

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.detail.DetailContent
import com.davcres.rickandmorty.feature.characters.detail.DetailUiIntent
import com.davcres.rickandmorty.feature.characters.detail.DetailUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val sampleCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episodeCount = 51,
    )

    @Test
    fun detailContent_loading_state_displays_loading_indicator() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                DetailContent(
                    state = DetailUiState(isLoading = true),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeTestRule.onNodeWithText(targetContext.getString(R.string.detail_loading)).assertIsDisplayed()
    }

    @Test
    fun detailContent_displays_character_attributes() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                DetailContent(
                    state = DetailUiState(character = sampleCharacter),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("${sampleCharacter.status} • ${sampleCharacter.species}").assertIsDisplayed()
        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
        composeTestRule.onNodeWithText("Earth (C-137)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Citadel of Ricks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scientist").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            targetContext.getString(R.string.detail_episodes_count, sampleCharacter.episodeCount)
        ).assertIsDisplayed()
    }

    @Test
    fun detailContent_back_button_triggers_BackClicked_intent() {
        var backDispatched = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                DetailContent(
                    state = DetailUiState(character = sampleCharacter),
                    onIntent = {
                        if (it is DetailUiIntent.BackClicked) backDispatched = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(
            targetContext.getString(R.string.nav_back_content_description)
        ).performClick()
        assertTrue(backDispatched)
    }

    @Test
    fun detailContent_bottom_back_button_triggers_BackClicked_intent() {
        var backDispatched = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                DetailContent(
                    state = DetailUiState(character = sampleCharacter),
                    onIntent = {
                        if (it is DetailUiIntent.BackClicked) backDispatched = true
                    },
                )
            }
        }

        val backButtonText = targetContext.getString(R.string.action_back)
        composeTestRule.onNodeWithText(backButtonText).assertIsDisplayed()
        composeTestRule.onNodeWithText(backButtonText).performClick()
        assertTrue(backDispatched)
    }

    @Test
    fun detailContent_error_state_displays_error_and_retries() {
        var retryDispatched = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                DetailContent(
                    state = DetailUiState(
                        character = null,
                        error = UiText.DynamicString("Unable to load character"),
                    ),
                    onIntent = {
                        if (it is DetailUiIntent.Retry) retryDispatched = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Unable to load character").assertIsDisplayed()
        val retryText = targetContext.getString(R.string.action_retry)
        composeTestRule.onNodeWithText(retryText).assertIsDisplayed()
        composeTestRule.onNodeWithText(retryText).performClick()
        assertTrue(retryDispatched)
    }
}
