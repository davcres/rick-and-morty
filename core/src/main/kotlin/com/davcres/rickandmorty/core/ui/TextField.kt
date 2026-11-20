package com.davcres.rickandmorty.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.extensions.modifiers.animations.animatedBorder
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme

private val DEFAULT_CORNER_RADIUS = 16.dp
private val CLEAR_BUTTON_SIZE = 40.dp
private val ICON_SIZE = 22.dp
private val ICON_SIZE_FOCUSED_EXTRA = 4.dp
private val TEXT_FIELD_HEIGHT = 52.dp
private val BORDER_STROKE_WIDTH = 1.dp
private val HORIZONTAL_PADDING = 16.dp
private val INNER_SPACING = 12.dp
private const val BG_ALPHA = 0.6f
private const val TINT_UNFOCUSED_ALPHA = 0.75f
private const val PLACEHOLDER_ALPHA = 0.7f
private const val ANIMATION_DURATION_MILLIS = 500

@Suppress("LongMethod")
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    contentDescription: String? = null,
    hasClearIcon: Boolean = false,
    cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(cornerRadius)
    val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = BG_ALPHA)
    val borderProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing
        ),
        label = "border-progress"
    )

    val animatedIconSize by animateDpAsState(
        targetValue = if (isFocused) {
            ICON_SIZE + ICON_SIZE_FOCUSED_EXTRA
        } else {
            ICON_SIZE
        },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = EaseIn
        ),
        label = "icon-size"
    )
    val animatedIconTint by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = TINT_UNFOCUSED_ALPHA)
        },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = EaseIn
        ),
        label = "icon-color"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(TEXT_FIELD_HEIGHT)
            .clip(shape)
            .background(bgColor)
            .animatedBorder(
                progress = borderProgress,
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = BORDER_STROKE_WIDTH,
                cornerRadius = cornerRadius
            )
            .padding(horizontal = HORIZONTAL_PADDING)
            .onFocusChanged { isFocused = it.isFocused }
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = animatedIconTint,
                        modifier = Modifier.size(animatedIconSize)
                    )

                    Spacer(Modifier.width(INNER_SPACING))
                }

                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = PLACEHOLDER_ALPHA)
                        )
                    }
                    innerTextField()
                }

                if (value.isNotEmpty() && hasClearIcon) {
                    Spacer(Modifier.width(INNER_SPACING))
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(CLEAR_BUTTON_SIZE)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Clear search",
                            tint = animatedIconTint,
                            modifier = Modifier.size(animatedIconSize)
                        )
                    }
                } else if (trailingIcon != null) {
                    Spacer(Modifier.width(INNER_SPACING))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = animatedIconTint,
                        modifier = Modifier.size(animatedIconSize)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun TextFieldPreview() {
    RickAndMortyTheme {
        TextField(
            value = "",
            onValueChange = {},
            leadingIcon = Icons.Default.Search,
            placeholder = "Search",
            contentDescription = "Search field",
            hasClearIcon = true,
            cornerRadius = 16.dp
        )
    }
}
