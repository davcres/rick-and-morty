package com.davcres.rickandmorty.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.R
import com.davcres.rickandmorty.core.ui.extensions.modifiers.animations.animatedBorder
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme

private val DEFAULT_CORNER_RADIUS = 16.dp
private val ICON_SIZE = 22.dp
private const val ICON_FOCUSED_SCALE = 1.18f
private val TEXT_FIELD_MIN_HEIGHT = 52.dp
private val BORDER_STROKE_WIDTH = 1.dp
private val HORIZONTAL_PADDING = 16.dp
private val INNER_SPACING = 12.dp
private val SUPPORTING_TEXT_TOP_PADDING = 4.dp
private const val BG_ALPHA = 0.6f
private const val TINT_UNFOCUSED_ALPHA = 0.75f
private const val PLACEHOLDER_ALPHA = 0.7f
private const val ANIMATION_DURATION_MILLIS = 350

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: String = "",
    contentDescription: String? = null,
    hasClearIcon: Boolean = false,
    clearIconContentDescription: String? = null,
    onClearClick: () -> Unit = { onValueChange("") },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    supportingText: String? = null,
    cornerRadius: Dp = DEFAULT_CORNER_RADIUS,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = BG_ALPHA)

    val activeBorderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    val borderProgress by animateFloatAsState(
        targetValue = if (isFocused || isError) 1f else 0f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        ),
        label = "border-progress",
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isFocused) ICON_FOCUSED_SCALE else 1f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        ),
        label = "icon-scale",
    )

    val animatedIconTint by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = TINT_UNFOCUSED_ALPHA)
        },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        ),
        label = "icon-color",
    )

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = TEXT_FIELD_MIN_HEIGHT)
                .clip(shape)
                .background(bgColor)
                .animatedBorder(
                    progress = borderProgress,
                    color = activeBorderColor,
                    strokeWidth = BORDER_STROKE_WIDTH,
                    cornerRadius = cornerRadius,
                )
                .padding(horizontal = HORIZONTAL_PADDING)
                .semantics {
                    if (contentDescription != null) {
                        this.contentDescription = contentDescription
                    }
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(activeBorderColor),
            decorationBox = { innerTextField ->
                TextFieldDecorationBox(
                    innerTextField = innerTextField,
                    value = value,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    hasClearIcon = hasClearIcon,
                    clearIconContentDescription = clearIconContentDescription,
                    onClearClick = onClearClick,
                    iconScale = iconScale,
                    iconTint = animatedIconTint,
                )
            },
        )

        TextFieldSupportingText(
            isError = isError,
            errorMessage = errorMessage,
            supportingText = supportingText,
        )
    }
}

@Composable
private fun TextFieldDecorationBox(
    innerTextField: @Composable () -> Unit,
    value: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    hasClearIcon: Boolean,
    clearIconContentDescription: String?,
    onClearClick: () -> Unit,
    iconScale: Float,
    iconTint: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(ICON_SIZE)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(LocalContentColor provides iconTint) {
                    leadingIcon()
                }
            }
            Spacer(Modifier.width(INNER_SPACING))
        }

        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = PLACEHOLDER_ALPHA),
                )
            }
            innerTextField()
        }

        TrailingContent(
            value = value,
            hasClearIcon = hasClearIcon,
            clearIconContentDescription = clearIconContentDescription,
            onClearClick = onClearClick,
            trailingIcon = trailingIcon,
            iconScale = iconScale,
            iconTint = iconTint,
        )
    }
}

@Composable
private fun TrailingContent(
    value: String,
    hasClearIcon: Boolean,
    clearIconContentDescription: String?,
    onClearClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)?,
    iconScale: Float,
    iconTint: Color,
) {
    val clearDescription = clearIconContentDescription ?: stringResource(R.string.core_action_clear)

    if (value.isNotEmpty() && hasClearIcon) {
        Spacer(Modifier.width(INNER_SPACING))
        IconButton(
            onClick = onClearClick,
            modifier = Modifier.semantics {
                contentDescription = clearDescription
            },
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(ICON_SIZE)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
            )
        }
    } else if (trailingIcon != null) {
        Spacer(Modifier.width(INNER_SPACING))
        Box(
            modifier = Modifier
                .size(ICON_SIZE)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides iconTint) {
                trailingIcon()
            }
        }
    }
}

@Composable
private fun TextFieldSupportingText(
    isError: Boolean,
    errorMessage: String?,
    supportingText: String?,
) {
    val textToDisplay = if (isError) errorMessage else supportingText
    if (textToDisplay != null) {
        Text(
            text = textToDisplay,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(
                start = HORIZONTAL_PADDING,
                top = SUPPORTING_TEXT_TOP_PADDING,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldPreview() {
    RickAndMortyTheme {
        TextField(
            value = "",
            onValueChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            placeholder = "Search",
            contentDescription = "Search field",
            hasClearIcon = true,
            cornerRadius = DEFAULT_CORNER_RADIUS,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldErrorPreview() {
    RickAndMortyTheme {
        TextField(
            value = "Invalid input",
            onValueChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            isError = true,
            errorMessage = "Name contains forbidden characters",
            hasClearIcon = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}
