package model.romanTableInfo

data class AtokRomanTableRow (
    /**
     * ローマ字
     */
    val romanAlphabet: String,
    /**
     * かな
     */
    val kana: String
): IRomanTableRow