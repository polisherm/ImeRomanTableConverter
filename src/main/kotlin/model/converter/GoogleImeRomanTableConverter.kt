package model.converter

import model.ImeType
import model.importer.GoogleImeRomanTableImporter
import model.importer.IImeRomanTableImporter
import model.romanTableInfo.GoogleRomanTableRow
import model.romanTableInfo.IRomanTableRow
import model.romanTableInfo.SkkRomanTableRow

class GoogleImeRomanTableConverter: IImeRomanTableConverter {
    override val importer: IImeRomanTableImporter = GoogleImeRomanTableImporter()

    override fun convert(romanTableInfoList: MutableList<IRomanTableRow>, convertTo: ImeType): String {
        if (romanTableInfoList[0] !is GoogleRomanTableRow) {
            throw IllegalArgumentException("GoogleRomanTableInfo型のみを許容します。")
        }

        return when (convertTo) {
            ImeType.ATOK -> TODO()
            ImeType.GOOGLE -> TODO()
            ImeType.SKK -> convertToSkk(romanTableInfoList)
        }

    }

    private fun convertToSkk(googleRomanTableInfoList: MutableList<IRomanTableRow>):String {
        val skkRomanTableInfoList = mutableListOf<SkkRomanTableRow>()

        googleRomanTableInfoList.forEach {
            // HACK: 不恰好なキャスト。しないで解決した方がいい。
            it as GoogleRomanTableRow

            // 「次の入力」が存在する場合はスキップする。
            if (it.nextInput != "") {
                return@forEach
            }

            val romanAlphabet = it.input
            val hiragana = it.export
            val katakana = convertToKatakana(hiragana)
            val halfWidthKana = convertToHalfWidthKana(katakana)

            val skkRomanTableInfo = SkkRomanTableRow(
                romanAlphabet,
                hiragana,
                katakana,
                halfWidthKana,
                0
            )
            skkRomanTableInfoList.add(skkRomanTableInfo)
        }

        // NOTE:
        //  tt->っt としてくれる機能はSKKでは促音モードをオンにする必要がある。
        //  Google日本語入力も「次の入力」という機能を用いて設定する必要があるが、
        //  これはSKKの促音モードに完全一致する機能ではない。
        //  従って、Google日本語入力の「次の入力」機能は無視して、プログラム上で設定を作成する。
        skkRomanTableInfoList.addAll(createSkkSokuonList())

        // NOTE:
        //  SKKでは記号類もローマ字テーブルに設定する必要がある。
        //  IMEオンの状態なら全ての記号を全角として出力するようにしている。 （CorvusSKKのデフォルトとは違う設定）
        skkRomanTableInfoList.addAll(createSkkSymbolList())

        // 同じく数字もローマ字テーブルに含めなければならない。
        skkRomanTableInfoList.addAll(createNumberList())

        // skkRomanTableInfoListの内容をタブ区切りのStringにして返す。
        // 重複は削除する。
        return skkRomanTableInfoList.distinct().joinToString("\n") {
            "${it.romanAlphabet}\t${it.hiragana}\t${it.katakana}\t${it.halfWidthKana}\t${it.mode}"
        }
    }

    /**
     * ひらがなを受けとってカタカナに変換して返す。
     * ひらがな以外が来た場合は元の文字を返す。
     */
    private fun convertToKatakana(hiragana: String): String {
        return hiragana.map {
            val char = it.code
            if (char in 0x3041..0x3096) {
                (char + 0x60).toChar()
            } else {
                it
            }
        }.joinToString("")
    }

    /**
     * カタカナを受けとって半角カタカナに変換して返す。
     * カタカナ以外が来た場合は元の文字を返す。
     */
    private fun convertToHalfWidthKana(katakana: String): String {
        return katakana.map {
            val char = it.code
            if (char in 0x30A1..0x30F6) {
                (char - 0x60).toChar()
            } else {
                it
            }
        }.joinToString("")
    }

    /**
     * SKKの促音設定に則した設定リストを返す。
     * TODO: SKK固有の処理なのでここに置くべきでない。
     */
    private fun createSkkSokuonList(): List<SkkRomanTableRow> {
        val result = mutableListOf<SkkRomanTableRow>()
        SkkSokuon.entries.forEach {
            val romanAlphabet = it.name
            val hiragana = "っ"
            val katakana = "ッ"
            val halfWidthKana = "ｯ"
            val mode = 1

            result.add(
                SkkRomanTableRow(
                    romanAlphabet,
                    hiragana,
                    katakana,
                    halfWidthKana,
                    mode
                )
            )
        }

        return result.toList()
    }

    private fun createSkkSymbolList(): List<SkkRomanTableRow> {
        val result = mutableListOf<SkkRomanTableRow>()

        Symbol.entries.forEach {
            result.add(
                SkkRomanTableRow(
                    it.symbol,
                    it.fullWidth,
                    it.fullWidth,
                    it.fullWidth,
                    0
                )
            )
        }

        return result.toList()
    }

    private fun createNumberList(): List<SkkRomanTableRow> {
        val result = mutableListOf<SkkRomanTableRow>()

        Number.entries.forEach {
            result.add(
                SkkRomanTableRow(
                    it.number,
                    it.number,
                    it.number,
                    it.number,
                    0
                )
            )
        }

        return result.toList()
    }

    // TODO: SKKの設定なのでこのクラス内に置くべきでない。
    private enum class SkkSokuon() {
        // 列挙子名をそのまま使うのでコーディング規約に則らない。(大文字にしない)
        bb,
        cc,
        dd,
        ff,
        gg,
        hh,
        jj,
        kk,
        pp,
        rr,
        ss,
        tt,
        vv,
        ww,
        xx,
        yy,
        zz
    }

    /**
     * SKKの場合は記号類もローマ字テーブルとして設定する必要がある
     * TODO: SKKの設定なのでこのクラス内に置くべきでない。
     */
    private enum class Symbol(val symbol: String, val fullWidth: String) {
        EXCLAMATION("!", "！"),
        DOUBLE_QUOTE("\"\"","”"),
        HASH("#", "＃"),
        DOLLAR("$", "＄"),
        PERCENT("%", "％"),
        AMPERSAND("&", "＆"),
        SINGLE_QUOTE("'", "’"),
        LEFT_PARENTHESIS("(", "（"),
        RIGHT_PARENTHESIS(")", "）"),
        ASTERISK("*", "＊"),
        PLUS("+", "＋"),
        COMMA(",", "、"),
        MINUS("-", "ー"),
        PERIOD(".", "。"),
        SLASH("/", "・"),
        COLON(":", "："),
        SEMICOLON(";", "；"),
        LESS_THAN("<", "＜"),
        EQUAL("=", "＝"),
        GREATER_THAN(">", "＞"),
        QUESTION("?", "？"),
        AT_SIGN("@", "＠"),
        LEFT_BRACKET("[", "「"),
        BACK_SLASH("\\", "￥"),
        RIGHT_BRACKET("]", "」"),
        CARET("^", "＾"),
        UNDER_SCORE("_", "＿"),
        GRAVE("`", "｀"),
        LEFT_BRACE("{", "｛"),
        PIPE("|", "｜"),
        RIGHT_BRACE("}", "｝"),
        TILDE("~", "～"),
        SPACE(" ", " ") // 空白は記号ではないがここに入れておく。SKKでは空白もローマ字テーブルに置いておく必要がある。
    }

    /**
     * 同じく数字もローマ字テーブルに含めなければならない。
     */
    private enum class Number(val number: String) {
        ZERO("0"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9")
    }

}