package com.solar.tabor

import org.junit.Test

import org.junit.Assert.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val ALGORITHM = "AES"

    private val soName = "libsh.so"
    private val progetName = "T713"
    private val name64 = "ahgo.xml"
    private val name32 = "ika.txt"

    // h5
//    private val soName = "libpics.so"
//    private val progetName = "T713"
//    private val name64 = "kiz.jpeg"
//    private val name32 = "eryu.zip"

    @Test
    fun addition_isCorrect() {
        val inputFile = "/Users/jxx/Desktop/soencode/$progetName/arm64-v8a/$soName"

        // 加密后文件路径 64
        val encryptedFile = "/Users/jxx/Desktop/soencode/$progetName/$name64"

        encrypt(File(inputFile).inputStream(), File(encryptedFile))

        val inputFile2 = "/Users/jxx/Desktop/soencode/$progetName/armeabi-v7a/$soName"
        // 加密后文件路径
        val encryptedFile2 = "/Users/jxx/Desktop/soencode/$progetName/$name32"
        encrypt(File(inputFile2).inputStream(), File(encryptedFile2))
    }


    private val SECRET_KEY = "opl12is4jz4h5szb".toByteArray() // 16, 24, or 32 bytes

    // 加密
    fun encrypt(inputStream: InputStream, outputFile: File) {
        val key = SecretKeySpec(
            SECRET_KEY, ALGORITHM
        )
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputStream = FileOutputStream(outputFile)
        val inputBytes = inputStream.readBytes()
        val outputBytes = cipher.doFinal(inputBytes)
        outputStream.write(outputBytes)
        outputStream.close()
        inputStream.close()
    }

    // 解密
    fun decrypt(inputFile: InputStream, outputFile: File) {
        val key = SecretKeySpec(
            SECRET_KEY, ALGORITHM
        )
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputStream = FileOutputStream(outputFile)
        val inputBytes = inputFile.readBytes()
        val outputBytes = cipher.doFinal(inputBytes)
        outputStream.write(outputBytes)
        outputStream.close()
        inputFile.close()
    }

}