package com.glass.manager.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.glass.manager.core.FileOps
import com.glass.manager.core.ZipTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun FilesScreen(dir: File, root: File, granted: Boolean, onDir: (File) -> Unit, onOpen: (File) -> Unit, onRecheck: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var tick by remember { mutableIntStateOf(0) }
    val items = remember(dir, tick, granted) { FileOps.list(dir) }
    BackHandler(dir != root) { dir.parentFile?.let(onDir) }

    Column(Modifier.fillMaxSize()) {
        TopBar("Berkas", dir.path.removePrefix(root.path).ifEmpty { "/" })
        if (!granted) {
            Column(Modifier.padding(18.dp).fillMaxWidth().glass().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Butuh izin akses semua berkas.")
                GlassButton("Beri akses", Icons.Rounded.Lock, primary = true) {
                    ctx.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${ctx.packageName}")))
                }
                GlassButton("Periksa lagi", Icons.Rounded.Refresh, onClick = onRecheck)
            }
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal = 18.dp, vertical = 8.dp).glass()) {
            items(items, key = { it.path }) { f ->
                val icon = when {
                    f.isDirectory -> Icons.Rounded.Folder
                    f.extension.equals("apk", true) -> Icons.Rounded.Android
                    f.extension.lowercase() in setOf("zip", "jar") -> Icons.Rounded.FolderZip
                    else -> Icons.Rounded.InsertDriveFile
                }
                GlassRow(icon, f.name, FileOps.sub(f), onClick = { if (f.isDirectory) onDir(f) else onOpen(f) })
            }
        }
        GlassButton("ZIP folder ini", Icons.Rounded.FolderZip, Modifier.padding(horizontal = 18.dp)) {
            scope.launch {
                val r = withContext(Dispatchers.IO) {
                    runCatching { ZipTools.compress(items, File(dir, "${dir.name.ifEmpty { "root" }}.zip")) }
                }
                tick++
                Toast.makeText(ctx, if (r.isSuccess) "ZIP dibuat" else "Gagal: ${r.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
