package model.importer

import model.romanTableInfo.IRomanTableInfo
import java.io.File

interface IImeRomanTableImporter {
    val tableInfoList: MutableList<IRomanTableInfo>

    /**
     * ローマ字テーブルをインポートして情報を抽出する。
     */
    fun import(file: File)
}