package com.glass.manager.core

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.FileHeader
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.File

object ZipTools {
    fun entries(zip: File): List<FileHeader> = ZipFile(zip).fileHeaders

    fun extractAll(zip: File, dest: File, password: String? = null) {
        val z = ZipFile(zip)
        if (!password.isNullOrEmpty()) z.setPassword(password.toCharArray())
        z.extractAll(dest.path)
    }

    fun compress(files: List<File>, out: File, password: String? = null) {
        val p = ZipParameters().apply {
            compressionMethod = CompressionMethod.DEFLATE
            if (!password.isNullOrEmpty()) { isEncryptFiles = true; encryptionMethod = EncryptionMethod.AES }
        }
        val z = if (password.isNullOrEmpty()) ZipFile(out) else ZipFile(out, password.toCharArray())
        files.forEach { if (it.isDirectory) z.addFolder(it, p) else z.addFile(it, p) }
    }

    fun addFile(zip: File, file: File) { ZipFile(zip).addFile(file) }
    fun remove(zip: File, names: List<String>) { ZipFile(zip).removeFiles(names) }
}
