package cn.mercury9.decoratedText

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

interface DecoratedText {
    val text: String

    @Composable
    fun compose() {
        compose(
            Color.Unspecified,
            null,
            null
        )
    }

    @Composable
    fun compose(
        color: Color,
        style: TextStyle?,
        textDecoration: TextDecoration?,
    )

    companion object {
        @Composable
        fun composeAll(
            texts: List<DecoratedText>,
            color: Color = Color.Unspecified,
            style: TextStyle? = null,
            textDecoration: TextDecoration? = null,
        ) {
            texts.forEach {
                it.compose(color, style, textDecoration)
            }
        }
    }
}
