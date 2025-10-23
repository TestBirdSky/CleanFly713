package com.majestic.grand.show.sssao

import java.io.File

// 类型别名定义
typealias FileName = String
typealias FilePath = String
typealias FileSize = Long

data class JunkFile(
    val name: FileName,
    val path: FilePath,
    val size: FileSize,
    val file: File,
    var isSelected: Boolean = true
) {
    // 重写equals方法，基于文件路径比较
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JunkFile

        if (path != other.path) return false

        return true
    }

    // 重写hashCode方法，基于文件路径计算
    override fun hashCode(): Int {
        return path.hashCode()
    }
    fun getSizeInMB(): String {
        val sizeInMB = size / (1024.0 * 1024.0)
        return if (sizeInMB < 1) {
            String.format("%.1f KB", size / 1024.0)
        } else {
            String.format("%.1f MB", sizeInMB)
        }
    }
}

data class JunkCategory(
    val name: String,
    val files: MutableList<JunkFile> = mutableListOf(),
    var isExpanded: Boolean = false,
    var isSelected: Boolean = true
) {
    companion object {
        @JvmField
        val MAX_FILES_PER_CATEGORY: Int = 500
    }

    fun getTotalSize(): FileSize {
        return files.sumOf { it.size }
    }

    fun getTotalSizeInMB(): String {
        val totalSize = getTotalSize()
        val sizeInMB = totalSize / (1024.0 * 1024.0)
        return if (sizeInMB < 1) {
            String.format("%.1f KB", totalSize / 1024.0)
        } else {
            String.format("%.1f MB", sizeInMB)
        }
    }

    fun getSelectedSize(): FileSize {
        return files.filter { it.isSelected }.sumOf { it.size }
    }

    fun updateSelectionState() {
        val selectedFiles = files.filter { it.isSelected }
        isSelected = selectedFiles.isNotEmpty() && selectedFiles.size == files.size
    }

    fun getFileCountInfo(): String {
        return if (files.size >= MAX_FILES_PER_CATEGORY) {
            "${files.size}+ files" // 显示+号表示可能有更多文件
        } else {
            "${files.size} files"
        }
    }
}