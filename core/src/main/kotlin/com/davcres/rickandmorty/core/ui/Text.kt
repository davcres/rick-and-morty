package com.davcres.rickandmorty.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    contentDescription: String? = null,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val finalModifier = if (contentDescription != null) {
        modifier.semantics {
            this.contentDescription = contentDescription
        }
    } else {
        modifier
    }

    BasicText(
        text = text,
        modifier = finalModifier,
        style = style.merge(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
        ),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    contentDescription: String? = null,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val finalModifier = if (contentDescription != null) {
        modifier.semantics {
            this.contentDescription = contentDescription
        }
    } else {
        modifier
    }

    BasicText(
        text = text,
        modifier = finalModifier,
        style = style.merge(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
        ),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        inlineContent = inlineContent,
    )
}

@Composable
fun Text(
    text: UiText,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    contentDescription: String? = null,
) {
    Text(
        text = text.asString(),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        contentDescription = contentDescription,
    )
}

@Preview(showBackground = true)
@Composable
private fun TextPreview() {
    RickAndMortyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Headline Large",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Body Medium with primary color",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = UiText.DynamicString("UiText dynamic string preview"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
