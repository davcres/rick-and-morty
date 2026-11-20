package com.davcres.rickandmorty.feature.characters.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

private val AVATAR_SIZE = 180.dp
private val AVATAR_CORNER_RADIUS = 24.dp
private val AVATAR_BORDER_WIDTH = 3.dp
private val STATUS_DOT_SIZE = 10.dp
private val STATUS_DOT_SPACING = 8.dp
private val STATUS_BADGE_PADDING_HORIZONTAL = 14.dp
private val STATUS_BADGE_PADDING_VERTICAL = 6.dp
private val STATUS_BADGE_CORNER_RADIUS = 20.dp
private val SPACING_SMALL = 8.dp
private val SPACING_MEDIUM = 16.dp
private const val BADGE_BACKGROUND_ALPHA = 0.5f

@Composable
fun DetailHeaderView(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
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
            modifier = Modifier
                .bounceClick()
                .size(AVATAR_SIZE)
                .clip(RoundedCornerShape(AVATAR_CORNER_RADIUS))
                .border(
                    width = AVATAR_BORDER_WIDTH,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(AVATAR_CORNER_RADIUS),
                ),
        )

        Spacer(modifier = Modifier.height(SPACING_MEDIUM))

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.bounceClick(),
        )

        Spacer(modifier = Modifier.height(SPACING_SMALL))

        Row(
            modifier = Modifier
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
}

private fun resolveStatusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> StatusColors.Alive
    "dead" -> StatusColors.Dead
    else -> StatusColors.Unknown
}
