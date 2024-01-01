package model

import ConverterUtil
import model.ImeType.*
import model.converter.GoogleImeRomanTableConverter
import model.converter.IImeRomanTableConverter
import model.importer.GoogleImeRomanTableImporter
import model.importer.IImeRomanTableImporter
import java.io.File

class ConverterModel {
    lateinit var imeType: ImeType

    fun openFile(): Boolean{
        val file = ConverterUtil.chooseFile()

        if (file.isEmpty) {
            println("ファイルを取得できませんでした。")
            return false
        }

        romanTableFile = file.get()

        // ファイルオープン時点でIMEのタイプは決定する。
        imeType = determineImeType()

        // ImporterとConverterを作っておく。
        when (imeType) {
            GOOGLE -> {
                importer = GoogleImeRomanTableImporter()
                converter = GoogleImeRomanTableConverter()
            }
            ATOK -> TODO()
            SKK -> TODO()
        }

        importer.import(romanTableFile)

        return true
    }

    fun getFileName(): String {
        return romanTableFile.name
    }

    fun convertTo(imeType: ImeType) {
        convertedResult = converter.convert(importer.tableInfoList, imeType)
    }

    private fun determineImeType(): ImeType {
        val firstLine = romanTableFile.readLines().first()

        // 最初の一行がスタイル情報であるならばATOK
        // ATOK Passport バージョン33.0.5の仕様
        // TODO: もっときれいに書けるはず。
        if (firstLine.length >= 4) {
            if (firstLine.subSequence(0,4) == "スタイル") {
                return ATOK
            }
        }


        // タブ区切りで要素を取得
        val elements = firstLine.split("\t")

        // 要素が5つならSKK
        // CorvusSKK ver.3.2.2に対応しています。
        if (elements.size == 5) {
            return SKK
        }

        return GOOGLE
    }

    private lateinit var romanTableFile: File
    private lateinit var converter: IImeRomanTableConverter
    private lateinit var importer: IImeRomanTableImporter
    var convertedResult = ""
}