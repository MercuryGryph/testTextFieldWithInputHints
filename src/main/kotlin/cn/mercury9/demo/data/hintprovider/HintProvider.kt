package cn.mercury9.demo.data.hintprovider


interface HintProvider {
    /**
     * A list of all hints got.
     */
    fun getHintFrom(source: String): List<String>
}

object NoHintProvider: HintProvider {
    override fun getHintFrom(source: String): List<String> = emptyList()
}
