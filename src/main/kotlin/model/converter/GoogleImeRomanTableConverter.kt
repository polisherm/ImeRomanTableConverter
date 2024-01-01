package model.converter

import ConverterUtil
import model.ImeType
import model.importer.GoogleImeRomanTableImporter
import model.importer.IImeRomanTableImporter
import model.romanTableInfo.GoogleRomanTableInfo
import model.romanTableInfo.IRomanTableInfo
import model.romanTableInfo.SkkRomanTableInfo

class GoogleImeRomanTableConverter: IImeRomanTableConverter {
    override val importer: IImeRomanTableImporter = GoogleImeRomanTableImporter()

    override fun convert(romanTableInfoList: MutableList<IRomanTableInfo>, convertTo: ImeType): String {
        if (romanTableInfoList[0] !is GoogleRomanTableInfo) {
            throw IllegalArgumentException("GoogleRomanTableInfo型のみを許容します。")
        }

        return when (convertTo) {
            ImeType.ATOK -> TODO()
            ImeType.GOOGLE -> TODO()
            ImeType.SKK -> convertToSkk(romanTableInfoList)
        }

    }

    private fun convertToSkk(googleRomanTableInfoList: MutableList<IRomanTableInfo>):String {
        val skkRomanTableInfoList = mutableListOf<SkkRomanTableInfo>()

        googleRomanTableInfoList.forEach {
            // HACK: 不恰好なキャスト。しないで解決した方がいい。
            it as GoogleRomanTableInfo

            // 「次の入力」が存在する場合はスキップする。
            if (it.nextInput != "") {
                return@forEach
            }

            val romanAlphabet = it.input
            val hiragana = it.export
            val katakana = convertToKatakana(hiragana)
            val halfWidthKana = convertToHalfWidthKana(katakana)

            val skkRomanTableInfo = SkkRomanTableInfo(
                romanAlphabet,
                hiragana,
                katakana,
                halfWidthKana,
                0
            )
            skkRomanTableInfoList.add(skkRomanTableInfo)
        }

        // tt->っt としてくれる機能はSKKでは促音モードをオンにする必要がある。
        // Google日本語入力も「次の入力」という機能を用いて設定する必要があるが、
        // これはSKKの促音モードに完全一致する機能ではない。
        // 従って、Google日本語入力の「次の入力」機能は無視して、プログラム上で設定を作成する。
        skkRomanTableInfoList.addAll(createSkkSokuonList())

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
    private fun createSkkSokuonList(): List<SkkRomanTableInfo> {
        val result = mutableListOf<SkkRomanTableInfo>()
        SkkSokuon.entries.forEach {
            val romanAlphabet = it.name
            val hiragana = "っ"
            val katakana = "ッ"
            val halfWidthKana = "ｯ"
            val mode = 1

            result.add(
                SkkRomanTableInfo(
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

    // TODO: SKKの設定なのでこのクラス内に置くべきでない。
    private enum class SkkSokuon(sokuon: String) {
        // 列挙子名をそのまま使うのでコーディング規約に則らない。(大文字にしない)
        bb("っ"),
        cc("っ"),
        dd("っ"),
        ff("っ"),
        gg("っ"),
        hh("っ"),
        jj("っ"),
        kk("っ"),
        pp("っ"),
        rr("っ"),
        ss("っ"),
        tt("っ"),
        vv("っ"),
        ww("っ"),
        xx("っ"),
        yy("っ"),
        zz("っ")
    }
}