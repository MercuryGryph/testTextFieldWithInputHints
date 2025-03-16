package cn.mercury9.demo.data.hintprovider

import androidx.compose.ui.text.TextRange

object NoHintProvider: HintProvider {
    override fun getHintFrom(source: String, textRange: TextRange): List<Hint> = emptyList()
}
