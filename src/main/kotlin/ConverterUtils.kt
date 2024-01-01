import java.io.File
import java.util.Optional
import javax.swing.JFileChooser
import javax.swing.JOptionPane

object ConverterUtil {
    fun chooseFile(): Optional<File> {
        // ファイル選択ダイアログを表示
        val fileChooser = JFileChooser()
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            // 選択されたファイルを取得
            val file = fileChooser.selectedFile
            return Optional.of(file)
        }
        return Optional.empty()
    }

    fun saveFile(converted: String) {
        // ファイル保存ダイアログを表示
        val saveFileChooser = object : JFileChooser() {
            override fun approveSelection() {
                // もし拡張子なしでパスが指定されたらここで拡張子（.txt）を付与する。
                if (selectedFile.extension.isEmpty()) {
                    selectedFile = File("${selectedFile.absolutePath}.txt")
                }

                if (selectedFile.exists() && dialogType == SAVE_DIALOG) {
                    val result = JOptionPane.showConfirmDialog(this, "同名ファイルが存在します。上書きしますか？", "確認", JOptionPane.YES_NO_CANCEL_OPTION)
                    when (result) {
                        JOptionPane.YES_OPTION -> super.approveSelection()
                        JOptionPane.NO_OPTION -> return
                        JOptionPane.CLOSED_OPTION -> return
                        JOptionPane.CANCEL_OPTION -> cancelSelection()
                    }
                }

                super.approveSelection()
            }
        }
        if (saveFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val saveFile = saveFileChooser.selectedFile
            saveFile.writeText(converted)
        }
    }
}