package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.core.ui.theme.StatusColors
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.R

private val AVATAR_SIZE = 76.dp
private val AVATAR_CORNER_RADIUS = 12.dp
private val STATUS_DOT_SIZE = 8.dp
private val STATUS_DOT_SPACING = 6.dp
private val CARD_CORNER_RADIUS = 16.dp
private val CARD_PADDING = 12.dp
private val SPACING_EXTRA_SMALL = 4.dp
private val SPACING_MEDIUM = 16.dp
private const val CARD_CONTAINER_ALPHA = 0.5f

@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CARD_CONTAINER_ALPHA),
        ),
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(
                    R.string.character_avatar_content_description,
                    character.name,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(AVATAR_SIZE)
                    .clip(RoundedCornerShape(AVATAR_CORNER_RADIUS)),
            )

            Spacer(modifier = Modifier.width(SPACING_MEDIUM))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
    }
}

private fun resolveStatusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> StatusColors.Alive
    "dead" -> StatusColors.Dead
    else -> StatusColors.Unknown
}
