package com.glass.manager.core

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileOps {
    fun list(dir: File): List<File> =
        (dir.listFiles()?.toList() ?: emptyList())
            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

    fun size(b: Long): String = when {
        b >= 1 shl 30 -> "%.1f GB".format(b / (1 shl 30).toDouble())
        b >= 1 shl 20 -> "%.1f MB".format(b / (1 shl 20).toDouble())
        b >= 1 shl 10 -> "%d KB".format(b shr 10)
        else -> "$b B"
    }

    fun date(f: File): String = SimpleDateFormat("d MMM yyyy", Locale("id")).format(Date(f.lastModified()))

    fun sub(f: File): String =
        if (f.isDirectory) "${f.list()?.size ?: 0} item · ${date(f)}" else "${size(f.length())} · ${date(f)}"
}
