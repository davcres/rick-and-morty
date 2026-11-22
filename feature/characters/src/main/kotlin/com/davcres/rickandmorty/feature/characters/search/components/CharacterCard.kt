package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.core.ui.theme.StatusColors
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.R
import com.davcres.rickandmorty.feature.characters.navigation.LocalAnimatedVisibilityScope
import com.davcres.rickandmorty.feature.characters.navigation.LocalSharedTransitionScope

private val AVATAR_SIZE = 76.dp
private val AVATAR_CORNER_RADIUS = 12.dp
private val AVATAR_BORDER_WIDTH = 2.dp
private const val AVATAR_BORDER_ALPHA = 0.6f
private val STATUS_DOT_SIZE = 8.dp
private val STATUS_DOT_SPACING = 6.dp
private val CARD_CORNER_RADIUS = 16.dp
private val CARD_PADDING = 12.dp
private val SPACING_EXTRA_SMALL = 4.dp
private val SPACING_MEDIUM = 16.dp
private const val CARD_CONTAINER_ALPHA = 0.5f

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    val cardSharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "character_card_${character.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(CARD_CORNER_RADIUS)),
            )
        }
    } else {
        Modifier
    }

    val avatarSharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "character_avatar_${character.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(AVATAR_CORNER_RADIUS)),
            )
        }
    } else {
        Modifier
    }

    val nameSharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "character_name_${character.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    } else {
        Modifier
    }

    Card(
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CARD_CONTAINER_ALPHA),
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(cardSharedBoundsModifier)
            .bounceClick(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterAvatar(
                character = character,
                modifier = avatarSharedBoundsModifier,
            )

            Spacer(modifier = Modifier.width(SPACING_MEDIUM))

            CharacterCardInfo(
                character = character,
                nameModifier = nameSharedBoundsModifier,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CharacterAvatar(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(AVATAR_SIZE)
            .clip(RoundedCornerShape(AVATAR_CORNER_RADIUS))
            .border(
                width = AVATAR_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.primary.copy(alpha = AVATAR_BORDER_ALPHA),
                shape = RoundedCornerShape(AVATAR_CORNER_RADIUS),
            ),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(character.imageUrl.ifBlank { null })
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_character_placeholder),
            error = painterResource(R.drawable.ic_character_fallback),
            fallback = painterResource(R.drawable.ic_character_fallback),
            contentDescription = stringResource(
                R.string.character_avatar_content_description,
                character.name,
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CharacterCardInfo(
    character: Character,
    nameModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = character.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = nameModifier,
        )

        Spacer(modifier = Modifier.height(SPACING_EXTRA_SMALL))

        Row(verticalAlignment = Alignment.CenterVertically) {
            val statusDotDescription = stringResource(R.string.character_status_dot_description)
            Box(
                modifier = Modifier
                    .size(STATUS_DOT_SIZE)
                    .clip(CircleShape)
                    .background(resolveStatusColor(character.status))
                    .semantics { contentDescription = statusDotDescription },
            )
            Spacer(modifier = Modifier.width(STATUS_DOT_SPACING))
            Text(
                text = "${character.status} - ${character.species}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (character.location.isNotBlank()) {
            Spacer(modifier = Modifier.height(SPACING_EXTRA_SMALL))
            Text(
                text = stringResource(R.string.character_last_known_location, character.location),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun resolveStatusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> StatusColors.Alive
    "dead" -> StatusColors.Dead
    else -> StatusColors.Unknown
}
