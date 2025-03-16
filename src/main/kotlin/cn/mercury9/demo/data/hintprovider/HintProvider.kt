package cn.mercury9.demo.data.hintprovider

import androidx.compose.ui.text.TextRange

interface HintProvider {
    /**
     * A list of all hints got.
     * @param source source text string
     * @param textRange witch part of the source string is selected
     */
    fun getHintFrom(source: String, textRange: TextRange): List<Hint>
}
