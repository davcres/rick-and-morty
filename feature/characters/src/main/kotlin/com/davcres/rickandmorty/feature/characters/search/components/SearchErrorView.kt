package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.Button
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.auxiliary.ButtonStyle
import com.davcres.rickandmorty.core.ui.theme.Color.Common.white
import com.davcres.rickandmorty.feature.characters.R

private val SCREEN_PADDING = 16.dp
private val SPACING_SMALL = 8.dp
private val SPACING_MEDIUM = 16.dp
private val STATE_ICON_SIZE = 64.dp
private val BUTTON_PADDING_HORIZONTAL = 20.dp
private val BUTTON_PADDING_VERTICAL = 10.dp

@Composable
fun SearchErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(STATE_ICON_SIZE),
            )
            Spacer(modifier = Modifier.height(SPACING_MEDIUM))
            Text(
                text = stringResource(R.string.search_error_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(SPACING_SMALL))
            Text(
                text = error.ifBlank { stringResource(R.string.search_error_fallback) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(SPACING_MEDIUM))
            Button(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                buttonStyle = ButtonStyle.PRIMARY,
                leftImageVector = Icons.Default.Refresh,
                tint = white,
                contentPadding = PaddingValues(
                    horizontal = BUTTON_PADDING_HORIZONTAL,
                    vertical = BUTTON_PADDING_VERTICAL,
                ),
            )
        }
    }
}
