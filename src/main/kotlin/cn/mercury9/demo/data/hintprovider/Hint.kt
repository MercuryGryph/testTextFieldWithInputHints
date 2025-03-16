package cn.mercury9.demo.data.hintprovider

import androidx.compose.ui.text.TextRange

/**
 * @param hint hint text string
 * @param textRange witch part of source string should be replaced with `hint`
 */
data class Hint(
    val hint: String,
    val textRange: TextRange,
) {
    companion object {
        val Empty = Hint("", TextRange.Zero)
    }
}
