package cn.mercury9.decoratedText

import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

class InCardDecoratedText(
    override var text: String,
): DecoratedText {

    @Composable
    override fun compose(
        color: Color,
        style: TextStyle?,
        textDecoration: TextDecoration?
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = " $text ",
                style = style ?: LocalTextStyle.current,
                color = color,
                textDecoration = textDecoration,
            )
        }
    }
}
