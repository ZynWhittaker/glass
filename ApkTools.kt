package com.glass.manager.core

import android.content.Context
import android.content.pm.PackageInfo
import com.android.apksig.ApkSigner
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.Date

object ApkTools {
    const val ALIAS = "glass"
    const val PASS = "android"

    fun info(ctx: Context, apk: File): PackageInfo? =
        ctx.packageManager.getPackageArchiveInfo(apk.path, 0)

    /** Buat keystore PKCS12 baru (RSA 2048, berlaku 25 tahun). */
    fun makeKey(out: File) {
        val kp = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val name = X500Name("CN=Glass Manager")
        val now = Date()
        val end = Date(now.time + 25L * 365 * 24 * 3600 * 1000)
        val builder = JcaX509v3CertificateBuilder(name, BigInteger.valueOf(now.time), now, end, name, kp.public)
        val signer = JcaContentSignerBuilder("SHA256withRSA").build(kp.private)
        val cert = JcaX509CertificateConverter().getCertificate(builder.build(signer))
        val ks = KeyStore.getInstance("PKCS12").apply { load(null, null) }
        ks.setKeyEntry(ALIAS, kp.private, PASS.toCharArray(), arrayOf(cert))
        out.outputStream().use { ks.store(it, PASS.toCharArray()) }
    }

    fun sign(input: File, output: File, keystore: File, v1: Boolean, v2: Boolean, v3: Boolean, v4: Boolean) {
        val ks = KeyStore.getInstance("PKCS12")
        keystore.inputStream().use { ks.load(it, PASS.toCharArray()) }
        val key = ks.getKey(ALIAS, PASS.toCharArray()) as PrivateKey
        val chain = ks.getCertificateChain(ALIAS).map { it as X509Certificate }
        val cfg = ApkSigner.SignerConfig.Builder(ALIAS, key, chain).build()
        val b = ApkSigner.Builder(listOf(cfg))
            .setInputApk(input)
            .setOutputApk(output)
            .setV1SigningEnabled(v1)
            .setV2SigningEnabled(v2)
            .setV3SigningEnabled(v3)
            .setV4SigningEnabled(v4)
        if (v4) b.setV4SignatureOutputFile(File(output.path + ".idsig"))
        b.build().sign()
    }
}
