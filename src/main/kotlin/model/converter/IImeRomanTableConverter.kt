package model.converter

import model.ImeType
import model.importer.IImeRomanTableImporter
import model.romanTableInfo.IRomanTableInfo

/**
 * ローマ字テーブル変換器のインターフェース
 * 変換器は各IMEごとに実装する。
 */
interface IImeRomanTableConverter {
    /**
     * 各IMEのローマ字テーブルの情報を抽出するためのインポーター
     */
    val importer: IImeRomanTableImporter

    /**
     * ローマ字テーブルを変換する
     * @param romanTableInfo
     * @return 変換後のローマ字テーブル
     */
    fun convert(romanTableInfoList: MutableList<IRomanTableInfo>, convertTo: ImeType): String
}