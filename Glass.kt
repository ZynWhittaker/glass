package com.glass.manager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Efek kaca: gradasi putih transparan + tepi highlight. (Blur latar sebenarnya = blob yang di-blur di belakang.) */
fun Modifier.glass(shape: Shape = RoundedCornerShape(24.dp)): Modifier = this
    .clip(shape)
    .background(Brush.linearGradient(listOf(Color.White.copy(.26f), Color.White.copy(.07f))))
    .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(.6f), Color.White.copy(.12f))), shape)

@Composable
private fun Blob(color: Color, modifier: Modifier) = Box(
    modifier.blur(60.dp).background(Brush.radialGradient(listOf(color.copy(.85f), Color.Transparent)), CircleShape)
)

@Composable
fun GlassBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF1C1250), Color(0xFF0B2B4D), Color(0xFF0E4A50)))
        )
    ) {
        Blob(Color(0xFFFF4FA3), Modifier.offset((-80).dp, 40.dp).size(260.dp))
        Blob(Color(0xFF27D3FF), Modifier.align(Alignment.CenterEnd).offset(110.dp, 0.dp).size(300.dp))
        Blob(Color(0xFF8A5BFF), Modifier.align(Alignment.BottomStart).offset(30.dp, 60.dp).size(260.dp))
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
    }
}

@Composable
fun TopBar(title: String, sub: String? = null, onBack: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (onBack != null) GlassIconButton(Icons.Rounded.ArrowBack, "Kembali", onBack)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            if (sub != null) Text(sub, fontSize = 12.sp, color = Color.White.copy(.78f), maxLines = 1)
        }
    }
}

@Composable
fun GlassIconButton(icon: ImageVector, desc: String, onClick: () -> Unit) =
    Box(Modifier.size(46.dp).glass(CircleShape).clickable(onClick = onClick), Alignment.Center) {
        Icon(icon, desc, tint = Color.White)
    }

@Composable
fun GlassButton(text: String, icon: ImageVector, modifier: Modifier = Modifier, primary: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    val base = if (primary) modifier.clip(shape)
        .background(Brush.horizontalGradient(listOf(Color(0xFF4F6BFF), Color(0xFF9440E6))))
        .border(1.dp, Color.White.copy(.45f), shape)
    else modifier.glass(shape)
    Row(
        base.clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 14.dp),
        Arrangement.Center, Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun GlassChip(text: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) =
    Box(
        modifier.height(40.dp).glass(CircleShape)
            .background(if (on) Color.White.copy(.28f) else Color.Transparent)
            .clickable(onClick = onClick),
        Alignment.Center
    ) { Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

@Composable
fun GlassRow(icon: ImageVector, title: String, sub: String, selected: Boolean = false, onClick: () -> Unit, onLong: (() -> Unit)? = null) =
    Row(
        Modifier.fillMaxWidth()
            .background(if (selected) Color.White.copy(.18f) else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color.White.copy(.18f)), Alignment.Center) {
            Icon(icon, null, tint = Color.White)
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(sub, fontSize = 12.sp, color = Color.White.copy(.78f), maxLines = 1)
        }
        if (selected) Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF8FA0FF))
    }

@Composable
fun BottomBar(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val items = listOf("Berkas" to Icons.Rounded.Folder, "Arsip" to Icons.Rounded.FolderZip,
        "APK" to Icons.Rounded.Android, "Setelan" to Icons.Rounded.Settings)
    Row(
        modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 18.dp, vertical = 14.dp)
            .height(68.dp).glass(CircleShape).padding(horizontal = 8.dp),
        Arrangement.SpaceAround, Alignment.CenterVertically
    ) {
        items.forEachIndexed { i, (name, icon) ->
            Column(
                Modifier.width(76.dp).height(54.dp).clip(CircleShape)
                    .background(if (i == selected) Color.White.copy(.28f) else Color.Transparent)
                    .clickable { onSelect(i) },
                Arrangement.Center, Alignment.CenterHorizontally
            ) {
                Icon(icon, name, tint = Color.White, modifier = Modifier.size(22.dp))
                Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
