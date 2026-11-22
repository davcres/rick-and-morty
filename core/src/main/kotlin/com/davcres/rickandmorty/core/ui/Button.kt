package com.davcres.rickandmorty.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.auxiliary.ButtonStyle
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.core.ui.extensions.modifiers.drawUnderline
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonPrimary
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonPrimaryDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonPrimaryDisabledText
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonSecondary
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonSecondaryDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTertiary
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTertiaryDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTogglePrimarySelected
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTogglePrimarySelectedDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTogglePrimarySelectedDisabledText
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTogglePrimaryUnselected
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonTogglePrimaryUnselectedDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonToggleSecondarySelected
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonToggleSecondarySelectedDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonToggleSecondarySelectedDisabledText
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonToggleSecondaryUnselected
import com.davcres.rickandmorty.core.ui.theme.Color.Button.ButtonToggleSecondaryUnselectedDisabled
import com.davcres.rickandmorty.core.ui.theme.Color.Common.white

private val BORDER_WIDTH = 1.dp
private val ICON_SIZE = 18.dp
private val ICON_SPACING = 8.dp
private val DEFAULT_SHAPE_CORNER = 12.dp
private val TERTIARY_SHAPE_CORNER = 4.dp
private val PADDING_HORIZONTAL = 16.dp
private val PADDING_VERTICAL = 10.dp

@Immutable
data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    fun containerColor(enabled: Boolean): Color = if (enabled) containerColor else disabledContainerColor

    fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}

object ButtonDefaults {
    val ContentPadding = PaddingValues(horizontal = PADDING_HORIZONTAL, vertical = PADDING_VERTICAL)
    val IconSpacing: Dp = ICON_SPACING
    val IconSize: Dp = ICON_SIZE
    val Shape: Shape = RoundedCornerShape(DEFAULT_SHAPE_CORNER)
    val TertiaryShape: Shape = RoundedCornerShape(TERTIARY_SHAPE_CORNER)

    fun colors(style: ButtonStyle, isSelected: Boolean = false): ButtonColors = when (style) {
        ButtonStyle.PRIMARY -> ButtonColors(
            containerColor = ButtonPrimary,
            contentColor = white,
            disabledContainerColor = ButtonPrimaryDisabled,
            disabledContentColor = ButtonPrimaryDisabledText,
        )
        ButtonStyle.SECONDARY -> ButtonColors(
            containerColor = white,
            contentColor = ButtonSecondary,
            disabledContainerColor = white,
            disabledContentColor = ButtonSecondaryDisabled,
        )
        ButtonStyle.TERTIARY -> ButtonColors(
            containerColor = Color.Transparent,
            contentColor = ButtonTertiary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ButtonTertiaryDisabled,
        )
        ButtonStyle.TOGGLE_PRIMARY -> resolveTogglePrimaryColors(isSelected)
        ButtonStyle.TOGGLE_SECONDARY -> resolveToggleSecondaryColors(isSelected)
    }

    fun border(
        style: ButtonStyle,
        enabled: Boolean,
        isSelected: Boolean = false,
    ): BorderStroke? = when (style) {
        ButtonStyle.SECONDARY -> {
            val color = if (enabled) ButtonSecondary else ButtonSecondaryDisabled
            BorderStroke(BORDER_WIDTH, color)
        }
        ButtonStyle.TOGGLE_PRIMARY -> if (!isSelected) {
            val color = if (enabled) ButtonTogglePrimaryUnselected else ButtonTogglePrimaryUnselectedDisabled
            BorderStroke(BORDER_WIDTH, color)
        } else {
            null
        }
        ButtonStyle.TOGGLE_SECONDARY -> if (!isSelected) {
            val color = if (enabled) ButtonToggleSecondaryUnselected else ButtonToggleSecondaryUnselectedDisabled
            BorderStroke(BORDER_WIDTH, color)
        } else {
            null
        }
        ButtonStyle.PRIMARY, ButtonStyle.TERTIARY -> null
    }

    private fun resolveTogglePrimaryColors(isSelected: Boolean): ButtonColors = if (isSelected) {
        ButtonColors(
            containerColor = ButtonTogglePrimarySelected,
            contentColor = white,
            disabledContainerColor = ButtonTogglePrimarySelectedDisabled,
            disabledContentColor = ButtonTogglePrimarySelectedDisabledText,
        )
    } else {
        ButtonColors(
            containerColor = white,
            contentColor = ButtonTogglePrimaryUnselected,
            disabledContainerColor = white,
            disabledContentColor = ButtonTogglePrimaryUnselectedDisabled,
        )
    }

    private fun resolveToggleSecondaryColors(isSelected: Boolean): ButtonColors = if (isSelected) {
        ButtonColors(
            containerColor = ButtonToggleSecondarySelected,
            contentColor = white,
            disabledContainerColor = ButtonToggleSecondarySelectedDisabled,
            disabledContentColor = ButtonToggleSecondarySelectedDisabledText,
        )
    } else {
        ButtonColors(
            containerColor = white,
            contentColor = ButtonToggleSecondaryUnselected,
            disabledContainerColor = white,
            disabledContentColor = ButtonToggleSecondaryUnselectedDisabled,
        )
    }
}

/**
 * Slot-based Button component adhering to Compose Design System guidelines.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonStyle: ButtonStyle = ButtonStyle.PRIMARY,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    shape: Shape = if (buttonStyle == ButtonStyle.TERTIARY) ButtonDefaults.TertiaryShape else ButtonDefaults.Shape,
    colors: ButtonColors = ButtonDefaults.colors(buttonStyle, isSelected),
    border: BorderStroke? = ButtonDefaults.border(buttonStyle, enabled, isSelected),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = colors.containerColor(enabled)
    val contentColor = colors.contentColor(enabled)
    val isToggle = buttonStyle == ButtonStyle.TOGGLE_PRIMARY || buttonStyle == ButtonStyle.TOGGLE_SECONDARY

    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics {
                if (isToggle) {
                    role = Role.Switch
                    selected = isSelected
                }
            }
            .bounceClick(),
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(
                    modifier = Modifier.padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    }
                    content()
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        trailingIcon()
                    }
                }
            }
        }
    }
}

/**
 * Convenience overload for standard text-and-icon buttons.
 */
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonStyle: ButtonStyle = ButtonStyle.PRIMARY,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    leftImageVector: ImageVector? = null,
    rightImageVector: ImageVector? = null,
    @DrawableRes leftDrawable: Int? = null,
    @DrawableRes rightDrawable: Int? = null,
    shape: Shape = if (buttonStyle == ButtonStyle.TERTIARY) ButtonDefaults.TertiaryShape else ButtonDefaults.Shape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    border: BorderStroke? = null,
    tint: Color = Color.Unspecified,
    underline: Boolean = false,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    buttonModifier: Modifier = modifier,
) {
    val actualModifier = if (buttonModifier != Modifier && modifier == Modifier) buttonModifier else modifier
    val leading = resolveButtonIcon(leftImageVector, leftDrawable, tint, contentDescription)
    val trailing = resolveButtonIcon(rightImageVector, rightDrawable, tint, contentDescription)

    Button(
        onClick = onClick,
        modifier = actualModifier,
        buttonStyle = buttonStyle,
        enabled = enabled,
        isSelected = isSelected,
        shape = shape,
        border = border ?: ButtonDefaults.border(buttonStyle, enabled, isSelected),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        leadingIcon = leading,
        trailingIcon = trailing,
    ) {
        val resolvedContentColor = LocalContentColor.current
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = resolvedContentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (underline) Modifier.drawUnderline(resolvedContentColor) else Modifier,
        )
    }
}

@Composable
private fun resolveButtonIcon(
    imageVector: ImageVector?,
    @DrawableRes drawableRes: Int?,
    tint: Color,
    contentDescription: String?,
): (@Composable () -> Unit)? = when {
    imageVector != null -> {
        {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = if (tint != Color.Unspecified) tint else LocalContentColor.current,
            )
        }
    }
    drawableRes != null -> {
        {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                colorFilter = if (tint != Color.Unspecified) ColorFilter.tint(tint) else null,
            )
        }
    }
    else -> null
}

data class ButtonPreviewParams(
    val buttonStyle: ButtonStyle,
    val showIcon: Boolean,
    val enabled: Boolean,
)

class ButtonPreviewParamsProvider : PreviewParameterProvider<ButtonPreviewParams> {
    override val values = getAllCombinations()

    private fun getAllCombinations(): Sequence<ButtonPreviewParams> {
        val samples = mutableListOf<ButtonPreviewParams>()

        ButtonStyle.entries.forEach { buttonStyle ->
            listOf(true, false).forEach { showIcon ->
                listOf(true, false).forEach { enabled ->
                    samples.add(
                        ButtonPreviewParams(
                            buttonStyle = buttonStyle,
                            showIcon = showIcon,
                            enabled = enabled,
                        ),
                    )
                }
            }
        }
        return samples.asSequence()
    }
}

@Preview
@Composable
fun ButtonPreview(
    @PreviewParameter(ButtonPreviewParamsProvider::class) buttonParams: ButtonPreviewParams,
) {
    Button(
        text = "Exit",
        onClick = {},
        buttonStyle = buttonParams.buttonStyle,
        underline = false,
        enabled = buttonParams.enabled,
    )
}
