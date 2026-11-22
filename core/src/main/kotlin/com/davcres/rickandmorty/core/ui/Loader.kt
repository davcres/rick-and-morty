package com.davcres.rickandmorty.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.R
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme

private val DEFAULT_DOT_SIZE = 12.dp
private const val DELAY_STEP_MILLIS = 250
private const val TOTAL_CYCLE_STEPS = 4
private const val BOUNCE_HEIGHT_RATIO = 0.75f
private const val SPACING_RATIO = 0.35f

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = DEFAULT_DOT_SIZE,
) {
    val density = LocalDensity.current
    val bounceHeight = dotSize * BOUNCE_HEIGHT_RATIO
    val bounceHeightPx = with(density) { bounceHeight.toPx() }
    val spacing = dotSize * SPACING_RATIO
    val totalDurationMillis = DELAY_STEP_MILLIS * TOTAL_CYCLE_STEPS

    val infiniteTransition = rememberInfiniteTransition(label = "loader_transition")

    val offset1Progress = rememberDotOffsetProgress(
        infiniteTransition = infiniteTransition,
        delayMillis = 0,
        totalDurationMillis = totalDurationMillis,
    )
    val offset2Progress = rememberDotOffsetProgress(
        infiniteTransition = infiniteTransition,
        delayMillis = DELAY_STEP_MILLIS,
        totalDurationMillis = totalDurationMillis,
    )
    val offset3Progress = rememberDotOffsetProgress(
        infiniteTransition = infiniteTransition,
        delayMillis = DELAY_STEP_MILLIS * 2,
        totalDurationMillis = totalDurationMillis,
    )

    val loadingLabel = stringResource(R.string.loading_cd)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(top = bounceHeight)
            .semantics {
                contentDescription = loadingLabel
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
    ) {
        Dot(
            offsetProgress = { offset1Progress.value },
            dotSize = dotSize,
            color = color,
            bounceHeightPx = bounceHeightPx,
        )
        Spacer(Modifier.width(spacing))
        Dot(
            offsetProgress = { offset2Progress.value },
            dotSize = dotSize,
            color = color,
            bounceHeightPx = bounceHeightPx,
        )
        Spacer(Modifier.width(spacing))
        Dot(
            offsetProgress = { offset3Progress.value },
            dotSize = dotSize,
            color = color,
            bounceHeightPx = bounceHeightPx,
        )
    }
}

@Composable
private fun rememberDotOffsetProgress(
    infiniteTransition: InfiniteTransition,
    delayMillis: Int,
    totalDurationMillis: Int,
): State<Float> {
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDurationMillis
                0f at delayMillis using FastOutSlowInEasing
                1f at (delayMillis + DELAY_STEP_MILLIS) using FastOutSlowInEasing
                0f at (delayMillis + DELAY_STEP_MILLIS * 2)
            },
        ),
        label = "dot_offset_$delayMillis",
    )
}

@Composable
private fun Dot(
    offsetProgress: () -> Float,
    dotSize: Dp,
    color: Color,
    bounceHeightPx: Float,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .size(dotSize)
            .graphicsLayer {
                translationY = -offsetProgress() * bounceHeightPx
            }
            .background(color = color, shape = CircleShape),
    )
}

@Preview(showBackground = true)
@Composable
private fun LoaderPreview() {
    RickAndMortyTheme {
        Loader(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoaderSmallPreview() {
    RickAndMortyTheme {
        Loader(
            dotSize = 8.dp,
            modifier = Modifier.padding(8.dp),
        )
    }
}
