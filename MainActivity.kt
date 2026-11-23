package com.glass.manager

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.glass.manager.ui.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme(colorScheme = darkColorScheme()) { App() } }
    }
}

@Composable
fun App() {
    val root = remember { Environment.getExternalStorageDirectory() }
    var tab by remember { mutableIntStateOf(0) }
    var dir by remember { mutableStateOf(root) }
    var zip by remember { mutableStateOf<File?>(null) }
    var apk by remember { mutableStateOf<File?>(null) }
    var granted by remember { mutableStateOf(Environment.isExternalStorageManager()) }

    GlassBackground {
        Box(Modifier.fillMaxSize().padding(bottom = 96.dp)) {
            when (tab) {
                0 -> FilesScreen(dir, root, granted, { dir = it }, { f ->
                    when (f.extension.lowercase()) {
                        "apk" -> { apk = f; tab = 2 }
                        "zip", "jar" -> { zip = f; tab = 1 }
                    }
                }, { granted = Environment.isExternalStorageManager() })
                1 -> ArchiveScreen(zip)
                2 -> ApkScreen(apk)
                else -> Column(Modifier.fillMaxSize()) {
                    TopBar("Setelan")
                    Text("Glass Manager 1.0\nAPK ini unsigned. Tanda tangani dulu sebelum dipasang.", Modifier.padding(24.dp))
                }
            }
        }
        BottomBar(tab, { tab = it }, Modifier.align(Alignment.BottomCenter))
    }
}
