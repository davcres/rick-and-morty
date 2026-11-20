package com.davcres.rickandmorty.core.ui.extensions.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.davcres.rickandmorty.core.ui.auxiliary.ButtonState

private const val DEFAULT_BOUNCE_SCALE = 0.95f

fun Modifier.bounceClick(
    scale: Float = DEFAULT_BOUNCE_SCALE,
    onClick: (() -> Unit)? = null,
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val animatedScale by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) scale else 1f,
        label = "bounce-scale",
    )

    val baseModifier = this.graphicsLayer {
        scaleX = animatedScale
        scaleY = animatedScale
    }

    val pointerModifier = baseModifier.pointerInput(buttonState) {
        awaitPointerEventScope {
            buttonState = if (buttonState == ButtonState.Pressed) {
                waitForUpOrCancellation()
                ButtonState.Idle
            } else {
                awaitFirstDown(false)
                ButtonState.Pressed
            }
        }
    }

    if (onClick != null) {
        pointerModifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        pointerModifier
    }
}
