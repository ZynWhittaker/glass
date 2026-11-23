package com.glass.manager.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.OutlinedTextField
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
fun ArchiveScreen(zip: File?) {
    Column(Modifier.fillMaxSize()) {
        if (zip == null) {
            TopBar("Arsip", "Belum ada arsip dibuka")
            Text("Ketuk berkas .zip di tab Berkas.", Modifier.padding(24.dp))
            return
        }
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()
        var tick by remember { mutableIntStateOf(0) }
        var pass by remember { mutableStateOf("") }
        val sel = remember(zip) { mutableStateListOf<String>() }
        val entries = remember(zip, tick) { runCatching { ZipTools.entries(zip) }.getOrDefault(emptyList()) }

        fun run(okMsg: String, block: () -> Unit) = scope.launch {
            val r = withContext(Dispatchers.IO) { runCatching(block) }
            tick++; sel.clear()
            Toast.makeText(ctx, if (r.isSuccess) okMsg else "Gagal: ${r.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
        }

        TopBar(zip.name, "${FileOps.size(zip.length())} · ${entries.size} berkas")
        LazyColumn(Modifier.weight(1f).padding(horizontal = 18.dp, vertical = 8.dp).glass()) {
            items(entries, key = { it.fileName }) { e ->
                val on = e.fileName in sel
                GlassRow(
                    if (e.isDirectory) Icons.Rounded.Folder else Icons.Rounded.InsertDriveFile,
                    e.fileName, FileOps.size(e.uncompressedSize), selected = on,
                    onClick = { if (on) sel.remove(e.fileName) else sel.add(e.fileName) }
                )
            }
        }
        OutlinedTextField(pass, { pass = it }, Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
            label = { Text("Sandi (opsional)") }, singleLine = true)
        Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassButton("Ekstrak", Icons.Rounded.Download, Modifier.weight(1f)) {
                run("Diekstrak ke folder ${zip.nameWithoutExtension}") {
                    ZipTools.extractAll(zip, File(zip.parentFile, zip.nameWithoutExtension), pass)
                }
            }
            GlassButton("Hapus", Icons.Rounded.Delete, Modifier.weight(1f)) {
                if (sel.isNotEmpty()) run("Dihapus dari arsip") { ZipTools.remove(zip, sel.toList()) }
            }
        }
    }
}
