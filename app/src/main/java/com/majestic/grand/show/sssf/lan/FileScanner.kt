package com.majestic.grand.show.sssf.lan

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 文件扫描工具类，用于扫描设备上的文件
 */
object FileScanner {
    
    // 文件类型筛选枚举
    enum class FileTypeFilter {
        ALL, IMAGE, VIDEO, AUDIO, DOCS, DOWNLOAD, ZIP
    }
    
    // 文件大小筛选枚举
    enum class FileSizeFilter {
        ALL, MB_10, MB_20, MB_50, MB_100, MB_200, MB_500
    }
    
    // 文件时间筛选枚举
    enum class FileTimeFilter {
        ALL, DAY_1, WEEK_1, MONTH_1, MONTH_3, MONTH_6
    }
    
    // 扫描设备上的所有文件
    suspend fun scanAllFiles(context: Context): List<FileItem> {
        val fileList = CopyOnWriteArrayList<FileItem>()
        
        withContext(Dispatchers.IO) {
            try {
                val directories = getScanDirectories(context)
                
                directories.forEach { directory ->
                    if (directory.exists() && directory.isDirectory) {
                        scanDirectory(directory, fileList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        return fileList
    }
    
    // 获取需要扫描的目录
    private fun getScanDirectories(context: Context): List<File> {
        val directories = mutableListOf<File>()
        
        // 添加外部存储目录
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            directories.add(Environment.getExternalStorageDirectory())
            
            // 添加常用目录
            val commonDirs = arrayOf(
                Environment.DIRECTORY_DOWNLOADS,
                Environment.DIRECTORY_DOCUMENTS,
                Environment.DIRECTORY_PICTURES,
                Environment.DIRECTORY_MOVIES,
                Environment.DIRECTORY_MUSIC
            )
            
            commonDirs.forEach {
                val dir = Environment.getExternalStoragePublicDirectory(it)
                if (dir.exists() && dir.isDirectory) {
                    directories.add(dir)
                }
            }
        }
        
        // 添加应用私有目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val appDirs = context.getExternalFilesDirs(null)
            appDirs.forEach { dir ->
                if (dir != null && dir.exists() && dir.isDirectory) {
                    directories.add(dir)
                }
            }
        }
        
        return directories
    }
    
    // 递归扫描目录
    private fun scanDirectory(directory: File, fileList: CopyOnWriteArrayList<FileItem>) {
        val files = directory.listFiles()
        
        if (files != null) {
            for (file in files) {
                if (Thread.currentThread().isInterrupted) {
                    break
                }
                
                if (file.isDirectory) {
                    // 跳过一些系统目录和缓存目录
                    if (!shouldSkipDirectory(file)) {
                        scanDirectory(file, fileList)
                    }
                } else {
                    // 只添加大于0字节的文件
                    if (file.length() > 0) {
                        val fileType = FileItem.getFileType(file)
                        val fileItem = FileItem(
                            id = UUID.randomUUID().toString(),
                            name = file.name,
                            path = file.absolutePath,
                            size = file.length(),
                            lastModified = file.lastModified(),
                            fileType = fileType
                        )
                        fileList.add(fileItem)
                    }
                }
            }
        }
    }
    
    // 检查是否应该跳过某个目录
    private fun shouldSkipDirectory(dir: File): Boolean {
        val dirName = dir.name.lowercase()
        return dirName.startsWith(".") || 
               dirName.contains("cache") || 
               dirName.contains("temp") || 
               dirName.contains("android")
    }
    
    // 根据筛选条件过滤文件
    fun filterFiles(
        files: List<FileItem>,
        typeFilter: FileTypeFilter,
        sizeFilter: FileSizeFilter,
        timeFilter: FileTimeFilter
    ): List<FileItem> {
        return files.filter { file ->
            filterByType(file, typeFilter) && 
            filterBySize(file, sizeFilter) && 
            filterByTime(file, timeFilter)
        }
    }
    
    // 根据文件类型筛选
    private fun filterByType(file: FileItem, typeFilter: FileTypeFilter): Boolean {
        if (typeFilter == FileTypeFilter.ALL) return true
        
        return when (typeFilter) {
            FileTypeFilter.IMAGE -> file.fileType == FileItem.FileType.IMAGE
            FileTypeFilter.VIDEO -> file.fileType == FileItem.FileType.VIDEO
            FileTypeFilter.AUDIO -> file.fileType == FileItem.FileType.AUDIO
            FileTypeFilter.DOCS -> file.fileType == FileItem.FileType.DOCS
            FileTypeFilter.DOWNLOAD -> {
                // 简单判断下载目录的文件
                file.path.lowercase().contains("download") || 
                file.fileType == FileItem.FileType.OTHER
            }
            FileTypeFilter.ZIP -> file.fileType == FileItem.FileType.ZIP
            else -> true
        }
    }
    
    // 根据文件大小筛选
    private fun filterBySize(file: FileItem, sizeFilter: FileSizeFilter): Boolean {
        if (sizeFilter == FileSizeFilter.ALL) return true
        
        val sizeInMB = file.size / (1024 * 1024.0)
        
        return when (sizeFilter) {
            FileSizeFilter.MB_10 -> sizeInMB > 10
            FileSizeFilter.MB_20 -> sizeInMB > 20
            FileSizeFilter.MB_50 -> sizeInMB > 50
            FileSizeFilter.MB_100 -> sizeInMB > 100
            FileSizeFilter.MB_200 -> sizeInMB > 200
            FileSizeFilter.MB_500 -> sizeInMB > 500
            else -> true
        }
    }
    
    // 根据文件时间筛选
    private fun filterByTime(file: FileItem, timeFilter: FileTimeFilter): Boolean {
        if (timeFilter == FileTimeFilter.ALL) return true
        
        val currentTime = System.currentTimeMillis()
        val fileTime = file.lastModified
        val diff = currentTime - fileTime
        
        // 转换为毫秒
        val day = 24 * 60 * 60 * 1000L
        val week = 7 * day
        val month = 30 * day
        
        return when (timeFilter) {
            FileTimeFilter.DAY_1 -> diff < day
            FileTimeFilter.WEEK_1 -> diff < week
            FileTimeFilter.MONTH_1 -> diff < month
            FileTimeFilter.MONTH_3 -> diff < 3 * month
            FileTimeFilter.MONTH_6 -> diff < 6 * month
            else -> true
        }
    }
}