import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import model.ConverterModel
import model.ImeType

/**
 * このアプリのビュークラス
 * UIコンポーネントを管理する。
 */
class ConverterView {
    @Composable
    fun show() {
        // ロードしたローマ字テーブルのファイル名
        var openedFileName by remember { mutableStateOf("") }

        // ロードしたローマ字テーブルのIMEの種類
        var imeTypeText by remember { mutableStateOf("") }

        val radioOptions = listOf("ATOK", "Google日本語入力", "SKK")

        // 分解宣言
        // MutableStateを2つの変数に分解している。componentNを持つクラスなら可能。MutableStateはそれを持つ。
        // selectedOptionは現在選択されているオプションの値を保持
        // onOptionSelectedは関数。この関数に新しい値を渡すことでselectedOptionの値を更新し、それに依存するコンポーザブルの再描画を引き起こす。
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

        // TODO: D&Dでファイルを開く
        // 以下はphindに聞いて提案されたコード
//    val target = object : DropTarget() {
//        @Synchronized
//        override fun drop(evt: DropTargetDropEvent) {
//            try {
//                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
//                val droppedFiles = evt
//                    .transferable.getTransferData(
//                        DataFlavor.javaFileListFlavor) as List<*>
//                droppedFiles.first()?.let {
//                    // ドロップされたファイルを処理します
//                    // ここではファイルの絶対パスを表示します
//                    println((it as File).absolutePath)
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//    }
//    AppManager.windows.first().window.contentPane.dropTarget = target


        MaterialTheme {
            Row(
                Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Column {
                    Button(onClick = {
                        val isSuccess = converterModel.openFile()
                        if (isSuccess) {
                            openedFileName = converterModel.getFileName()
                            imeTypeText = converterModel.imeType.imeName
                        }
                    }) {
                        Text("開く")
                    }

                    Text(openedFileName)

                    Text(imeTypeText)
                }

                // 参考: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
                Column(
                    Modifier
                        .selectableGroup()
                ) {
                    radioOptions.forEach { text ->
                        Row(
                            Modifier
//                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    Button(onClick = {
                        converterModel.convertTo(ImeType.valueOf(selectedOption))
                        ConverterUtil.saveFile(converterModel.convertedResult)

                        // TODO: 変換完了ダイアログを表示したい。
                    }) {
                        Text("変換")
                    }
                }
            }
        }
    }

    private val converterModel = ConverterModel()
}