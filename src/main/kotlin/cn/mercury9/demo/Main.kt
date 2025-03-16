package cn.mercury9.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cn.mercury9.demo.ui.App

fun main() = application {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
