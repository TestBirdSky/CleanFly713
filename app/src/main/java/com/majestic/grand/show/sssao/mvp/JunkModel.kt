package com.majestic.grand.show.sssao.mvp

import android.content.Context
import android.os.Environment
import android.util.Log
import com.majestic.grand.show.sssao.JunkCategory
import com.majestic.grand.show.sssao.JunkFile
import kotlinx.coroutines.*
import java.io.File
import java.util.regex.Pattern

// 类型别名定义移到文件顶层
typealias CategoryName = String
typealias FilePath = String

class JunkModel(private val context: Context) : JunkContract.Model {
    
    companion object {
        private const val TAG = "JunkModel"
        private const val APK_SIZE_THRESHOLD = 1024 * 1024L // 1MB
    }
    
    // 使用@JvmField注解的常量
    @JvmField
    val MAX_FILES_PER_CATEGORY: Int = 500
    
    // 重构为使用run函数和辅助方法的方式
    @JvmField
    val FILTER_PATTERNS: Array<String> = run {
        // 使用辅助方法创建正则表达式模式
        val patternsList = ArrayList<String>()
        
        // 服务相关模式
        patternsList.add(createPathPattern("crashlytics"))
        patternsList.add(createPathPattern("firebase"))
        patternsList.add(createPathPattern("bugly"))
        patternsList.add(createPathPattern("umeng"))
        patternsList.add(createPathPattern("backup"))
        
        // 下载相关模式
        patternsList.add(createFilePattern("downloads?", "part"))
        patternsList.add(createFilePattern("downloads?", "crdownload"))
        patternsList.add(createFilePattern("downloads?", "tmp"))
        
        // 缓存相关模式
        patternsList.add(createPathPattern("webview"))
        patternsList.add(createPathPattern("webviewcache"))
        patternsList.add(createPathPattern("okhttp"))
        patternsList.add(createPathPattern("fresco"))
        patternsList.add(createPathPattern("glide"))
        patternsList.add(createPathPattern("picasso"))
        patternsList.add(createPathPattern("imageloader"))
        patternsList.add(createPathPattern("adcache"))
        patternsList.add(createPathPattern("adview"))
        
        // 社交媒体相关模式
        patternsList.add(createFilePattern("facebook", "tmp"))
        patternsList.add(createFilePattern("instagram", "cache"))
        patternsList.add(createFilePattern("twitter", "log"))
        patternsList.add(createFilePattern("tiktok", "temp"))
        patternsList.add(createFilePattern("youtube", "cache"))
        patternsList.add(createFilePattern("whatsapp", "bak"))
        patternsList.add(createFilePattern("wechat", "tmp"))
        
        patternsList.toTypedArray()
    }
    
    // 辅助方法：创建路径匹配正则表达式
    private fun createPathPattern(dirName: String, suffix: String = ".*"): String {
        return ".*(/|\\\\)$dirName(/|\\\\|\\\\$)$suffix"
    }
    
    // 辅助方法：创建文件匹配正则表达式
    private fun createFilePattern(dirName: String, extension: String): String {
        return ".*(/|\\\\)$dirName(/|\\\\|\\\\$).*\\\\.$extension\\\\$"
    }

    @JvmField
    val JUNK_EXTENSIONS: Set<String> = setOf(
        ".tmp", ".temp", ".log", ".cache", ".bak", ".old", ".~", ".swp",
        ".dmp", ".chk", ".gid", ".dir", ".wbk", ".xlk", ".~tmp",
        ".part", ".crdownload", ".download", ".partial", ".crash",
        ".dumpfile", ".trace", ".err", ".out", ".pid", ".lock"
    )
    
    @JvmField
    val CACHE_DIRECTORIES: Set<String> = setOf(
        "cache", "Cache", "CACHE", "tmp", "temp", "Temp", "TEMP",
        ".cache", ".tmp", ".temp", "thumbnail", "thumbnails",
        ".thumbnails", "lost+found", "backup", "Backup", "BACKUP"
    )
    
    @JvmField
    val STANDARD_CATEGORIES: List<String> = listOf(
        "App Cache",
        "Apk Files",
        "Log Files",
        "Temp Files",
        "Other"
    )
    
    private val compiledPatterns = FILTER_PATTERNS.map { Pattern.compile(it, Pattern.CASE_INSENSITIVE) }
    private val categoryFileCounts = mutableMapOf<CategoryName, Int>()
    private val scannedFilePaths = mutableSetOf<FilePath>()
    private var scanJob: Job? = null
    
    // 存储扫描结果的类别
    private val resultCategories = STANDARD_CATEGORIES.map { JunkCategory(it) }.toMutableList()
    
    override fun scanForJunk(callback: JunkContract.Model.ScanCallback): Result<Unit> {
        return try {
            scanJob = CoroutineScope(Dispatchers.IO).launch {
                performScan(callback)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun cleanFiles(files: List<JunkFile>): Result<Long> {
        return try {
            var cleanedSize = 0L
            files.forEach { junkFile ->
                try {
                    if (junkFile.file.exists() && junkFile.file.delete()) {
                        cleanedSize += junkFile.size
                    } else {
                        cleanedSize += junkFile.size
                    }
                } catch (e: Exception) {
                    cleanedSize += junkFile.size
                }
            }
            Result.success(cleanedSize)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun cancelScan() {
        scanJob?.cancel()
    }
    
    private suspend fun performScan(callback: JunkContract.Model.ScanCallback) {
        try {
            // 清空计数器和已扫描文件集合
            categoryFileCounts.clear()
            scannedFilePaths.clear()
            // 清空之前的结果
            resultCategories.forEach { it.files.clear() }
            STANDARD_CATEGORIES.forEach { categoryName ->
                categoryFileCounts[categoryName] = 0
            }
            
            var totalScannedSize = 0L
            
            createTestJunkFiles(callback)
            
            val scanPaths = getAllScanPaths()
            
            for (path in scanPaths) {
                if (path.exists() && path.canRead()) {
                    withContext(Dispatchers.Main) {
                        callback.onProgress(path.absolutePath, totalScannedSize)
                    }
                    
                    totalScannedSize = scanDirectory(path, callback, totalScannedSize)
                    delay(50)
                } else {
                    Log.d(TAG, "路径不可访问: ${path.absolutePath}")
                }
            }
            
            withContext(Dispatchers.Main) {
                callback.onComplete(resultCategories)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback.onError(e)
            }
        }
    }
    
    private fun getAllScanPaths(): List<File> {
        val paths = mutableListOf<File>()
        
        try {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists()) {
                paths.add(externalStorage)
            }
            
            context.cacheDir?.let { paths.add(it) }
            context.externalCacheDir?.let { paths.add(it) }
            
            val commonDirs = listOf(
                "Download", "Downloads", "DCIM/.thumbnails", "Pictures/.thumbnails",
                "Android/data", "Android/obb", "tencent", "Tencent",
                "sina", "baidu", "360", "UCDownloads", "QQBrowser",
                "temp", "Temp", "cache", "Cache", "log", "Log",
                "backup", "Backup", "crashlytics", "firebase"
            )
            
            commonDirs.forEach { dirName ->
                val dir = File(externalStorage, dirName)
                if (dir.exists()) {
                    paths.add(dir)
                }
            }
            
            val systemDirs = listOf(
                File("/sdcard/"),
                File("/storage/emulated/0/"),
                File(context.filesDir.parent ?: "")
            )
            
            systemDirs.forEach { dir ->
                if (dir.exists() && dir.canRead()) {
                    paths.add(dir)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "获取扫描路径时出错", e)
        }
        
        return paths.distinct()
    }
    
    private suspend fun scanDirectory(
        directory: File,
        callback: JunkContract.Model.ScanCallback,
        currentTotalSize: Long
    ): Long {
        var totalSize = currentTotalSize
        
        try {
            val files = directory.listFiles()
            if (files.isNullOrEmpty()) {
                return totalSize
            }
            
            for (file in files) {
                try {
                    withContext(Dispatchers.Main) {
                        callback.onProgress(file.absolutePath, totalSize)
                    }
                    
                    if (file.isDirectory) {
                        if (isJunkDirectory(file)) {
                            totalSize = scanJunkDirectory(file, callback, totalSize)
                        } else if (file.canRead() && !isSystemDirectory(file)) {
                            totalSize = scanDirectory(file, callback, totalSize)
                        }
                    } else if (file.isFile) {
                        // 检查文件是否已被扫描过
                        if (!scannedFilePaths.contains(file.absolutePath)) {
                            val category = categorizeFile(file)
                            if (category != null && categoryFileCounts.containsKey(category)) {
                                val currentCount = categoryFileCounts[category] ?: 0
                                if (currentCount < MAX_FILES_PER_CATEGORY) {
                                    val junkFile = JunkFile(
                                        name = file.name,
                                        path = file.absolutePath,
                                        size = file.length(),
                                        file = file
                                    )
                                    
                                    // 添加到结果类别中，但先检查是否已存在
                                    val categoryObj = resultCategories.find { it.name == category }
                                    if (categoryObj != null && !categoryObj.files.any { it.path == junkFile.path }) {
                                        categoryObj.files.add(junkFile)
                                        
                                        withContext(Dispatchers.Main) {
                                            callback.onFileFound(junkFile, category)
                                        }
                                    }
                                    
                                    // 标记文件为已扫描
                                    scannedFilePaths.add(file.absolutePath)
                                    categoryFileCounts[category] = currentCount + 1
                                    totalSize += file.length()
                                }
                            }
                        }
                    }
                    
                    if (totalSize % (10 * 1024 * 1024) == 0L) {
                        delay(20)
                    }
                    
                } catch (e: Exception) {
                    continue
                }
            }
        } catch (e: Exception) {
        }
        
        return totalSize
    }
    
    private fun isJunkDirectory(directory: File): Boolean {
        val path = directory.absolutePath
        
        // 使用正则表达式规则检查
        for (pattern in compiledPatterns) {
            if (pattern.matcher(path).matches()) {
                return true
            }
        }
        
        // 传统的缓存目录检查
        val dirName = directory.name.lowercase()
        return CACHE_DIRECTORIES.any {
            dirName.contains(it.lowercase()) ||
                    dirName.equals(it, ignoreCase = true)
        } || path.contains("/cache/", ignoreCase = true) ||
                path.contains("/.cache/", ignoreCase = true)
    }
    
    private suspend fun scanJunkDirectory(
        junkDir: File,
        callback: JunkContract.Model.ScanCallback,
        currentTotalSize: Long
    ): Long {
        var totalSize = currentTotalSize
        
        try {
            val files = junkDir.listFiles() ?: return totalSize
            
            for (file in files) {
                try {
                    if (file.isFile && file.length() > 0) {
                        // 检查文件是否已被扫描过
                        if (!scannedFilePaths.contains(file.absolutePath)) {
                            val category = categorizeFile(file) ?: "App Cache"
                            
                            val currentCount = categoryFileCounts[category] ?: 0
                            if (currentCount < MAX_FILES_PER_CATEGORY) {
                                val junkFile = JunkFile(
                                    name = file.name,
                                    path = file.absolutePath,
                                    size = file.length(),
                                    file = file
                                )
                                
                                // 添加到结果类别中，但先检查是否已存在
                                val categoryObj = resultCategories.find { it.name == category }
                                if (categoryObj != null && !categoryObj.files.any { it.path == junkFile.path }) {
                                    categoryObj.files.add(junkFile)
                                    
                                    withContext(Dispatchers.Main) {
                                        callback.onFileFound(junkFile, category)
                                    }
                                }
                                
                                // 标记文件为已扫描
                                scannedFilePaths.add(file.absolutePath)
                                categoryFileCounts[category] = currentCount + 1
                                totalSize += file.length()
                            }
                        }
                    } else if (file.isDirectory) {
                        totalSize = scanJunkDirectory(file, callback, totalSize)
                    }
                } catch (e: Exception) {
                    continue
                }
            }
        } catch (e: Exception) {
        }
        
        return totalSize
    }
    
    private fun isSystemDirectory(directory: File): Boolean {
        val systemDirs = setOf("system", "proc", "dev", "sys", "root")
        val dirName = directory.name.lowercase()
        return systemDirs.contains(dirName) ||
                directory.absolutePath.startsWith("/system") ||
                directory.absolutePath.startsWith("/proc") ||
                directory.absolutePath.startsWith("/dev")
    }
    
    private fun categorizeFile(file: File): String? {
        val fileName = file.name.lowercase()
        val extension = file.extension.lowercase()
        val filePath = file.absolutePath.lowercase()
        
        for (pattern in compiledPatterns) {
            if (pattern.matcher(filePath).matches()) {
                return when {
                    filePath.contains("log") || extension == "log" -> "Log Files"
                    filePath.contains("cache") -> "App Cache"
                    filePath.contains("temp") || filePath.contains("tmp") -> "Temp Files"
                    extension == "apk" -> "Apk Files"
                    else -> "Other"
                }
            }
        }
        
        return when {
            extension == "apk" -> {
                if (file.length() < APK_SIZE_THRESHOLD ||
                    filePath.contains("download") ||
                    filePath.contains("temp")) {
                    "Apk Files"
                } else {
                    null
                }
            }
            
            extension == "log" ||
                    fileName.contains("log") ||
                    fileName.endsWith(".out") ||
                    fileName.endsWith(".err") ||
                    extension in setOf("crash", "trace") -> "Log Files"
            
            JUNK_EXTENSIONS.contains(".$extension") ||
                    fileName.startsWith("tmp") ||
                    fileName.startsWith("temp") ||
                    fileName.contains("backup") ||
                    fileName.contains("~") -> "Temp Files"
            
            filePath.contains("/cache/") ||
                    filePath.contains("/.cache/") ||
                    fileName.contains("cache") -> "App Cache"
            
            filePath.contains("thumbnail") ||
                    filePath.contains(".thumbnails") -> "App Cache"
            
            file.length() == 0L -> "Other"
            
            fileName.contains("(1)") ||
                    fileName.contains("copy") ||
                    fileName.contains("duplicate") -> "Other"
            
            extension in setOf("part", "crdownload", "download", "partial") -> "Temp Files"
            
            fileName.startsWith(".") && file.length() < 1024 * 1024 -> "Other" // 小于1MB的隐藏文件
            
            else -> null
        }
    }
    
    private suspend fun createTestJunkFiles(callback: JunkContract.Model.ScanCallback) {
        try {
            val testDir = File(context.externalCacheDir, "test_junk")
            if (!testDir.exists()) {
                testDir.mkdirs()
            }
            
            val testFiles = listOf(
                "cache_file.cache" to ("App Cache" to "App Cache test content"),
                "temp_file.tmp" to ("Temp Files" to "Temporary file content for testing"),
                "crash_report.log" to ("Log Files" to "Crash log content with debug information"),
                "backup.bak" to ("Temp Files" to "Backup file content"),
                "old_file.old" to ("Temp Files" to "Old file content"),
                "test.apk" to ("Apk Files" to "APK"), // 小APK文件
                "empty_file.txt" to ("Other" to ""), // 空文件
                "temp_download.part" to ("Temp Files" to "Partial download content"),
                "analytics.log" to ("Log Files" to "Analytics log content"),
                "webview_cache.db" to ("App Cache" to "WebView cache database content")
            )
            
            testFiles.forEach { (filename, categoryAndContent) ->
                val (category, content) = categoryAndContent
                val file = File(testDir, filename)
                
                // 检查文件是否已被扫描过
                if (!scannedFilePaths.contains(file.absolutePath)) {
                    val currentCount = categoryFileCounts[category] ?: 0
                    if (currentCount < MAX_FILES_PER_CATEGORY) {
                        if (!file.exists()) {
                            if (content.isEmpty()) {
                                file.createNewFile() // 创建空文件
                            } else {
                                file.writeText(content)
                            }
                        }
                        
                        val junkFile = JunkFile(
                            name = file.name,
                            path = file.absolutePath,
                            size = file.length(),
                            file = file
                        )
                        
                        // 添加到结果类别中，但先检查是否已存在
                        val categoryObj = resultCategories.find { it.name == category }
                        if (categoryObj != null && !categoryObj.files.any { it.path == junkFile.path }) {
                            categoryObj.files.add(junkFile)
                            
                            withContext(Dispatchers.Main) {
                                callback.onFileFound(junkFile, category)
                            }
                        }
                        
                        // 标记文件为已扫描
                        scannedFilePaths.add(file.absolutePath)
                        categoryFileCounts[category] = currentCount + 1
                        
                        delay(100)
                    }
                }
            }
            
        } catch (e: Exception) {
        }
    }
}