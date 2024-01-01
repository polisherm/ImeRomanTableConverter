package model.romanTableInfo

/**
 * SKKのローマ字テーブル設定の情報を格納する。
 * 各変数が各列に対応する。
 */
data class SkkRomanTableInfo (
    /**
     * ローマ字
     */
    val romanAlphabet: String,
    /**
     * ひらがな
     */
    val hiragana: String,
    /**
     * カタカナ
     */
    val katakana: String,
    /**
     * ｶﾀｶﾅ
     */
    val halfWidthKana: String,
    /**
     * SKK固有の設定
     * 詳細は以下リンクを参照
     * https://github.com/nathancorvussolis/corvusskk?tab=readme-ov-file#%E3%83%AD%E3%83%BC%E3%83%9E%E5%AD%97%E4%BB%AE%E5%90%8D%E5%A4%89%E6%8F%9B%E8%A1%A8
     *
     * 設定なし:0
     * 促音/撥音(ん):1
     * 待機:2
     * 促音/撥音(ん)+待機:3
     */
    val mode: Int
): IRomanTableInfo
