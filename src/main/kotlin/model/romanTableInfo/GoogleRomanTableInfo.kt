package model.romanTableInfo

/**
 * Google日本語入力のローマ字テーブル設定の情報を格納する。
 * 各変数が各列に対応する。
 */
data class GoogleRomanTableInfo(
    /**
     * 「入力」
     */
    val input: String,
    /**
     * 「出力」
     */
    val export: String,
    /**
     * 「次の入力」
     * Google日本語入力特有の情報
     * 例えば「った」のように「促音+た行」の入力をするとき、「tt」まで入力することで「っt」となるが、
     * Google日本語入力では、
     * 入力：tt
     * 出力：っ
     * 次の入力：t
     * という設定を明示的に行わなければならない。
     * (柔軟な設定ができるとも言える)
     */
    val nextInput: String
): IRomanTableInfo
