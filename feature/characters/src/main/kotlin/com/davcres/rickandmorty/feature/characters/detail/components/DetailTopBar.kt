package com.davcres.rickandmorty.feature.characters.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.feature.characters.R

private val TOP_BAR_PADDING_HORIZONTAL = 8.dp
private val TOP_BAR_PADDING_VERTICAL = 8.dp
private val BACK_BUTTON_SIZE = 40.dp
private val ICON_SIZE = 24.dp

@Composable
fun DetailTopBar(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TOP_BAR_PADDING_HORIZONTAL, vertical = TOP_BAR_PADDING_VERTICAL),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(BACK_BUTTON_SIZE)
                .clip(CircleShape)
                .bounceClick(onClick = onBackClicked),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.nav_back_content_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(ICON_SIZE),
            )
        }

        Text(
            text = stringResource(R.string.detail_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
