package com.majestic.grand.show.sssf.lan

import com.majestic.grand.show.StorageUtils
import java.io.File

/**
 * 文件数据模型类，用于存储文件信息
 */
data class FileItem(
    val id: String, // 唯一标识符
    val name: String, // 文件名
    val path: String, // 文件路径
    val size: Long, // 文件大小（字节）
    val lastModified: Long, // 最后修改时间
    val fileType: FileType, // 文件类型
    var isSelected: Boolean = false // 是否被选中
) {
    // 获取文件大小的格式化字符串
    fun getFormattedSize(): String {
        return StorageUtils.formatBytes(size)
    }
    
    // 获取文件类型的枚举
    enum class FileType {
        IMAGE, VIDEO, AUDIO, DOCS, DOWNLOAD, ZIP, OTHER
    }
    
    companion object {
        // 根据文件路径和扩展名确定文件类型
        fun getFileType(file: File): FileType {
            val extension = getFileExtension(file).lowercase()
            return when {
                extension in arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp") -> FileType.IMAGE
                extension in arrayOf("mp4", "avi", "mov", "mkv", "webm") -> FileType.VIDEO
                extension in arrayOf("mp3", "wav", "flac", "aac", "ogg") -> FileType.AUDIO
                extension in arrayOf("doc", "docx", "pdf", "txt", "xls", "xlsx", "ppt", "pptx") -> FileType.DOCS
                extension in arrayOf("zip", "rar", "7z", "tar", "gz") -> FileType.ZIP
                // 可以根据需要添加更多类型
                else -> FileType.OTHER
            }
        }
        
        // 获取文件扩展名
        private fun getFileExtension(file: File): String {
            val name = file.name
            val lastDotIndex = name.lastIndexOf('.')
            return if (lastDotIndex > 0 && lastDotIndex < name.length - 1) {
                name.substring(lastDotIndex + 1)
            } else {
                ""
            }
        }
    }
}