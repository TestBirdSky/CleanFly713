package com.majestic.grand.show.sssao.mvp

import com.majestic.grand.show.sssao.JunkCategory
import com.majestic.grand.show.sssao.JunkFile
import java.io.File

/**
 * MVP Contract interfaces for junk scanning and cleaning functionality
 */
interface JunkContract {
    
    interface View {
        fun showScanProgress(currentPath: String, scannedSize: Long)
        fun addFileToCategory(junkFile: JunkFile, categoryName: String)
        fun showScanComplete(categories: List<JunkCategory>)
        fun showError(error: String)
        fun updateCleanProgress(progress: Int)
        fun showCleanComplete(cleanedSize: Long)
        fun updateTotalInfo(totalFileCount: Int)
    }
    
    interface Presenter {
        fun startScan()
        fun stopScan()
        fun startClean(selectedFiles: List<JunkFile>)
        fun onFileSelected()
        fun onDestroy()
    }
    
    interface Model {
        interface ScanCallback {
            fun onProgress(currentPath: String, scannedSize: Long)
            fun onFileFound(junkFile: JunkFile, categoryName: String)
            fun onComplete(categories: List<JunkCategory>)
            fun onError(error: Exception)
        }
        
        fun scanForJunk(callback: ScanCallback): Result<Unit>
        fun cleanFiles(files: List<JunkFile>): Result<Long>
        fun cancelScan()
    }
}