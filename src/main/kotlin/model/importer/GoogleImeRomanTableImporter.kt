package model.importer

import model.romanTableInfo.GoogleRomanTableInfo
import model.romanTableInfo.IRomanTableInfo
import java.io.File

class GoogleImeRomanTableImporter: IImeRomanTableImporter {
    override fun import(file: File) {
        val lines = file.readLines()

        lines.forEach { line ->
            if (line.isEmpty()) {
                return@forEach
            }

            // タブ区切りで要素を取得する。
            val elements = line.split("\t")

            // 1列目: 入力
            val input = elements[0]
            // 2列目: 出力
            val export = elements[1]
            // 3列目は次の入力。オプショナルなのでリストに存在しない場合は空文字を入れる。
            val nextInput = if (elements.size == 3) {
                elements[2]
            } else {
                ""
            }

            tableInfoList.add(GoogleRomanTableInfo(input, export, nextInput))
        }
    }

    /**
     * Google日本語入力のローマ字テーブル設定の情報を格納するリスト
     */
    override val tableInfoList: MutableList<IRomanTableInfo> = mutableListOf()
}