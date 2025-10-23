package com.two.smooth

import org.junit.Test

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val ALGORITHM = "AES"

    private val pathBASE = "/Users/jxx/AndroidStudioProjects/T697/CleanT697/CoreD/"

    @Test
    fun addition_dex() {
        val sourceFilePath = "${pathBASE}makejar/dex/classes.dex" // 源文件路径，可按需修改
        val outputFolderPath = "${pathBASE}output" // 目标文件路径，可按需修改
        val sourceFile = File(sourceFilePath)
        val outputFolder = File(outputFolderPath)
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        val local1 = File("$outputFolderPath/local1.txt")
        val file3 = File("$outputFolderPath/iosk.gif")
        val string = dexToAesText(sourceFile)

        local1.writeText(string)

        println("文件重写并保存成功")

        // 验证
        file3.writeText(string)
        // aes+iv 加密
        val restoredDex = File(outputFolderPath, "dexMy2.dex")
        val dexBytes = decryptDex(DEX_AES_KEY, file3.readText())
        FileOutputStream(restoredDex).use { it.write(dexBytes) }
    }

    private val DEX_AES_KEY = "ki2j4z3m6j7aks12".toByteArray() // 16, 24, or 32 bytes


    // DEX -> AES加密文本
    fun dexToAesText(dexFile: File): String {
        val dexBytes = dexFile.readBytes()
        val encrypted = encrypt(dexBytes)
        return Base64.getEncoder().encodeToString(encrypted)
    }


    // 加密
    fun encrypt(inputBytes: ByteArray): ByteArray {
        val key = SecretKeySpec(DEX_AES_KEY, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)
        return outputBytes
    }

    // 解密
    private fun decryptDex(keyAes: ByteArray, inStr: String): ByteArray {
        val inputBytes = Base64.getDecoder().decode(inStr)
        val key = SecretKeySpec(keyAes, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)
        return outputBytes
    }

}