package com.davcres.rickandmorty.feature.characters.detail.components

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
import androidx.compose.ui.text.style.TextAlign
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

private val AVATAR_SIZE = 180.dp
private val AVATAR_CORNER_RADIUS = 24.dp
private val AVATAR_BORDER_WIDTH = 3.dp
private const val AVATAR_BORDER_ALPHA = 0.6f
private val STATUS_DOT_SIZE = 10.dp
private val STATUS_DOT_SPACING = 8.dp
private val STATUS_BADGE_PADDING_HORIZONTAL = 14.dp
private val STATUS_BADGE_PADDING_VERTICAL = 6.dp
private val STATUS_BADGE_CORNER_RADIUS = 20.dp
private val SPACING_SMALL = 8.dp
private val SPACING_MEDIUM = 16.dp
private const val BADGE_BACKGROUND_ALPHA = 0.5f

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailHeaderView(
    character: Character,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DetailAvatar(
            character = character,
            modifier = avatarSharedBoundsModifier,
        )

        Spacer(modifier = Modifier.height(SPACING_MEDIUM))

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .bounceClick()
                .then(nameSharedBoundsModifier),
        )

        Spacer(modifier = Modifier.height(SPACING_SMALL))

        DetailStatusBadge(character = character)
    }
}

@Composable
private fun DetailAvatar(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .bounceClick()
            .then(modifier)
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
private fun DetailStatusBadge(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .bounceClick()
            .clip(RoundedCornerShape(STATUS_BADGE_CORNER_RADIUS))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = BADGE_BACKGROUND_ALPHA)
            )
            .padding(
                horizontal = STATUS_BADGE_PADDING_HORIZONTAL,
                vertical = STATUS_BADGE_PADDING_VERTICAL,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
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
            text = "${character.status} • ${character.species}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun resolveStatusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> StatusColors.Alive
    "dead" -> StatusColors.Dead
    else -> StatusColors.Unknown
}
