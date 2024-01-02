package model.importer

import model.romanTableInfo.IRomanTableRow
import java.io.File

interface IImeRomanTableImporter {
    val tableInfoList: MutableList<IRomanTableRow>

    /**
     * ローマ字テーブルをインポートして情報を抽出する。
     */
    fun import(file: File)
}