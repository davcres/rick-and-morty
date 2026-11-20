package com.davcres.rickandmorty.feature.characters.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.R

private val CARD_CORNER_RADIUS = 20.dp
private val CARD_PADDING = 16.dp
private val ROW_SPACING_VERTICAL = 12.dp
private val ICON_SIZE = 22.dp
private val ICON_SPACING = 12.dp
private const val CARD_CONTAINER_ALPHA = 0.45f
private const val DIVIDER_ALPHA = 0.2f

@Composable
fun DetailInfoCard(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = CARD_CONTAINER_ALPHA),
        ),
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(ROW_SPACING_VERTICAL),
        ) {
            Text(
                text = stringResource(R.string.detail_section_info),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            DetailInfoRow(
                icon = Icons.Default.Person,
                label = stringResource(R.string.detail_label_gender),
                value = character.gender.ifBlank { "Unknown" },
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = DIVIDER_ALPHA)
            )

            DetailInfoRow(
                icon = Icons.Default.Place,
                label = stringResource(R.string.detail_label_origin),
                value = character.origin.ifBlank { "Unknown" },
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = DIVIDER_ALPHA)
            )

            DetailInfoRow(
                icon = Icons.Default.Place,
                label = stringResource(R.string.detail_label_location),
                value = character.location.ifBlank { "Unknown" },
            )

            if (character.type.isNotBlank()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = DIVIDER_ALPHA)
                )

                DetailInfoRow(
                    icon = Icons.Default.Info,
                    label = stringResource(R.string.detail_label_type),
                    value = character.type,
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = DIVIDER_ALPHA)
            )

            DetailInfoRow(
                icon = Icons.Default.PlayArrow,
                label = stringResource(R.string.detail_label_episodes),
                value = stringResource(R.string.detail_episodes_count, character.episodeCount),
            )
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(ICON_SIZE),
        )

        Spacer(modifier = Modifier.width(ICON_SPACING))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
