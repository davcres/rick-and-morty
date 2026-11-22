package com.davcres.rickandmorty.feature.characters

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.search.SearchContent
import com.davcres.rickandmorty.feature.characters.search.SearchUiIntent
import com.davcres.rickandmorty.feature.characters.search.SearchUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

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
    fun initial_state_displays_search_initial_view() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithText(targetContext.getString(R.string.search_placeholder)).assertIsDisplayed()
        composeTestRule.onNodeWithText(targetContext.getString(R.string.search_initial_title)).assertIsDisplayed()
    }

    @Test
    fun typing_in_search_bar_triggers_QueryChanged_intent() {
        var dispatchedIntent: SearchUiIntent? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(),
                    onIntent = { dispatchedIntent = it },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(
            targetContext.getString(R.string.search_content_description)
        ).performTextInput("Morty")

        assertTrue(dispatchedIntent is SearchUiIntent.QueryChanged)
        assertEquals("Morty", (dispatchedIntent as SearchUiIntent.QueryChanged).query)
    }

    @Test
    fun clicking_clear_button_clears_query_and_triggers_intent() {
        var dispatchedIntent: SearchUiIntent? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(query = "Rick"),
                    onIntent = { dispatchedIntent = it },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        assertTrue(dispatchedIntent is SearchUiIntent.QueryChanged)
        assertEquals("", (dispatchedIntent as SearchUiIntent.QueryChanged).query)
    }

    @Test
    fun loading_state_displays_loading_indicator() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(isLoading = true, characters = emptyList()),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Test
    fun empty_state_displays_no_characters_found_message() {
        val query = "NonExistentName"
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(
                        query = query,
                        characters = emptyList(),
                        isLoading = false,
                    ),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithText(targetContext.getString(R.string.search_empty_title)).assertIsDisplayed()
    }

    @Test
    fun error_state_displays_error_and_retry_button_triggers_Retry_intent() {
        var retryDispatched = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(
                        query = "Rick",
                        error = UiText.DynamicString("Network failure"),
                    ),
                    onIntent = {
                        if (it is SearchUiIntent.Retry) retryDispatched = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Network failure").assertIsDisplayed()
        val retryText = targetContext.getString(R.string.action_retry)
        composeTestRule.onNodeWithText(retryText).assertIsDisplayed()
        composeTestRule.onNodeWithText(retryText).performClick()
        assertTrue(retryDispatched)
    }

    @Test
    fun success_state_displays_character_list_and_handles_click() {
        var clickedCharacterId: Int? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(
                        query = "Rick",
                        characters = listOf(sampleCharacter),
                    ),
                    onIntent = {
                        if (it is SearchUiIntent.CharacterClicked) clickedCharacterId = it.characterId
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rick Sanchez").performClick()
        assertEquals(sampleCharacter.id, clickedCharacterId)
    }

    @Test
    fun pagination_loading_displays_footer_loader() {
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(
                        query = "Rick",
                        characters = listOf(sampleCharacter),
                        canPaginate = true,
                        isLoadingMore = true,
                    ),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Test
    fun pagination_error_displays_retry_button_and_triggers_LoadMore() {
        var loadMoreDispatched = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                SearchContent(
                    state = SearchUiState(
                        query = "Rick",
                        characters = listOf(sampleCharacter),
                        canPaginate = true,
                        paginationError = UiText.DynamicString("Failed to load more"),
                    ),
                    onIntent = {
                        if (it is SearchUiIntent.LoadMore) loadMoreDispatched = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Failed to load more").assertIsDisplayed()
        val retryPaginationText = targetContext.getString(R.string.pagination_retry)
        composeTestRule.onNodeWithText(retryPaginationText).assertIsDisplayed()
        composeTestRule.onNodeWithText(retryPaginationText).performClick()
        assertTrue(loadMoreDispatched)
    }
}
