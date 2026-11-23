package com.glass.manager.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glass.manager.core.ApkTools
import com.glass.manager.core.FileOps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ApkScreen(apk: File?) {
    Column(Modifier.fillMaxSize()) {
        if (apk == null) {
            TopBar("Alat APK", "Belum ada APK dipilih")
            Text("Ketuk berkas .apk di tab Berkas.", Modifier.padding(24.dp))
            return
        }
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()
        var v1 by remember { mutableStateOf(false) }
        var v2 by remember { mutableStateOf(true) }
        var v3 by remember { mutableStateOf(true) }
        var v4 by remember { mutableStateOf(false) }
        val info = remember(apk) { ApkTools.info(ctx, apk) }
        val ks = File(ctx.filesDir, "glass.p12")

        fun toast(s: String) = Toast.makeText(ctx, s, Toast.LENGTH_LONG).show()
        fun run(okMsg: String, block: () -> Unit) = scope.launch {
            val r = withContext(Dispatchers.IO) { runCatching(block) }
            toast(if (r.isSuccess) okMsg else "Gagal: ${r.exceptionOrNull()?.message}")
        }

        TopBar("Alat APK", apk.name)
        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(Modifier.fillMaxWidth().glass().padding(16.dp)) {
                Text(info?.packageName ?: "APK tidak terbaca", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("v${info?.versionName ?: "?"} · ${FileOps.size(apk.length())}", fontSize = 12.sp, color = Color.White.copy(.78f))
            }
            Text("Skema tanda tangan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassChip("V1", v1, Modifier.weight(1f)) { v1 = !v1 }
                GlassChip("V2", v2, Modifier.weight(1f)) { v2 = !v2 }
                GlassChip("V3", v3, Modifier.weight(1f)) { v3 = !v3 }
                GlassChip("V4", v4, Modifier.weight(1f)) { v4 = !v4 }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassButton("Edit DEX", Icons.Rounded.Code, Modifier.weight(1f)) { toast("Belum tersedia (lihat README)") }
                GlassButton("Manifest", Icons.Rounded.Description, Modifier.weight(1f)) { toast("Belum tersedia (lihat README)") }
            }
            GlassButton("Buat keystore baru", Icons.Rounded.Key, Modifier.fillMaxWidth()) {
                run("Keystore dibuat (alias ${ApkTools.ALIAS}, sandi ${ApkTools.PASS})") { ApkTools.makeKey(ks) }
            }
            GlassButton("Tanda tangani APK", Icons.Rounded.Edit, Modifier.fillMaxWidth(), primary = true) {
                if (!ks.exists()) { toast("Buat keystore dulu"); return@GlassButton }
                val out = File(apk.parentFile, apk.nameWithoutExtension + "-signed.apk")
                run("Tersimpan: ${out.name}") { ApkTools.sign(apk, out, ks, v1, v2, v3, v4) }
            }
        }
    }
}
