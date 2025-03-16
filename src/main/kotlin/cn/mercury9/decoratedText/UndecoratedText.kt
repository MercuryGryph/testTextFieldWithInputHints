package cn.mercury9.decoratedText

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

class UndecoratedText(
    override val text: String,
) : DecoratedText {

    @Composable
    override fun compose(
        color: Color,
        style: TextStyle?,
        textDecoration: TextDecoration?,
    ) {
        Text(
            text = text,
            color = color,
            style = style ?: LocalTextStyle.current,
            textDecoration = textDecoration,
        )
    }

    companion object {
        fun splitWhitespace(
            text: String,
        ): List<UndecoratedText> {
            val result = mutableListOf<UndecoratedText>()
            val cache = StringBuilder()

            text.forEach {
                if (it.isWhitespace()) {
                    if (cache.isNotEmpty()) {
                        result += UndecoratedText(cache.toString())
                    }
                    result += UndecoratedText(it.toString())
                    cache.clear()
                } else {
                    cache.append(it)
                }
            }

            if (cache.isNotEmpty()) {
                result += UndecoratedText(cache.toString())
            }

            return result
        }
    }
}
