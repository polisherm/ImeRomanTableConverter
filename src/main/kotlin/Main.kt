import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import javax.swing.UIManager

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    // SwingのUIコンポーネント要素を
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    // 中のコンテンツに合わせてリサイズする。
    // 参考:https://stackoverflow.com/questions/73103076/how-do-i-set-the-window-size-to-fit-the-contents-in-jetpack-compose
    val windowState = rememberWindowState(size = DpSize.Unspecified)

    Window(
        onCloseRequest = ::exitApplication,
        title = "IME Converter",
        state = windowState
    ) {
//        App()
        ConverterView().show()
    }
}
