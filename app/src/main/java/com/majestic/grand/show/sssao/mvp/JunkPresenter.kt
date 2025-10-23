package com.majestic.grand.show.sssao.mvp

import android.content.Context
import com.majestic.grand.show.sssao.JunkCategory
import com.majestic.grand.show.sssao.JunkFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JunkPresenter(
    private val context: Context,
    private val view: JunkContract.View
) : JunkContract.Presenter {
    
    private val model: JunkContract.Model = JunkModel(context)
    private val categories = mutableListOf<JunkCategory>()
    private var totalFileCount = 0
    private var lastUpdateTime = 0L
    
    init {
        initializeStandardCategories()
    }
    
    private fun initializeStandardCategories() {
        val standardCategoryNames = listOf("App Cache", "Apk Files", "Log Files", "Temp Files", "Other")
        categories.clear()
        standardCategoryNames.forEach { categoryName ->
            categories.add(JunkCategory(categoryName))
        }
    }
    
    override fun startScan() {
        CoroutineScope(Dispatchers.Main).launch {
            model.scanForJunk(object : JunkContract.Model.ScanCallback {
                override fun onProgress(currentPath: String, scannedSize: Long) {
                    view.showScanProgress(currentPath, scannedSize)
                }
                
                override fun onFileFound(junkFile: JunkFile, categoryName: String) {
                    addFileToCategory(junkFile, categoryName)
                }
                
                override fun onComplete(categories: List<JunkCategory>) {
                    // 更新本地类别列表
                    this@JunkPresenter.categories.clear()
                    this@JunkPresenter.categories.addAll(categories)
                    view.showScanComplete(categories)
                }
                
                override fun onError(error: Exception) {
                    view.showError(error.message ?: "Unknown error occurred")
                }
            })
        }
    }
    
    override fun stopScan() {
        model.cancelScan()
    }
    
    override fun startClean(selectedFiles: List<JunkFile>) {
        CoroutineScope(Dispatchers.Main).launch {
            view.updateCleanProgress(0)
            
            if (selectedFiles.isEmpty()) {
                view.showError("No selected files need to be cleaned")
                return@launch
            }
            
            val result = withContext(Dispatchers.IO) {
                model.cleanFiles(selectedFiles)
            }
            
            result.fold(
                onSuccess = { cleanedSize ->
                    view.showCleanComplete(cleanedSize)
                },
                onFailure = { exception ->
                    view.showError(exception.message ?: "Error occurred during cleaning")
                }
            )
        }
    }
    
    override fun onFileSelected() {
        // This will be called when files are selected/deselected
        // The view will handle UI updates based on selection state
    }
    
    override fun onDestroy() {
        model.cancelScan()
    }
    
    private fun addFileToCategory(junkFile: JunkFile, categoryName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val category = categories.find { it.name == categoryName }
            if (category != null) {
                if (category.files.size < JunkCategory.MAX_FILES_PER_CATEGORY) {
                    // 检查文件是否已存在，通过文件路径判断
                    val isFileAlreadyExists = category.files.any { it.path == junkFile.path }
                    
                    if (!isFileAlreadyExists) {
                        category.files.add(junkFile)
                        totalFileCount++
                        
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastUpdateTime > 200) {
                            lastUpdateTime = currentTime
                            view.addFileToCategory(junkFile, categoryName)
                            view.updateTotalInfo(totalFileCount)
                        }
                    }
                }
            }
        }
    }
    
    fun getCategories(): List<JunkCategory> = categories
    
    fun getSelectedFiles(): List<JunkFile> {
        val selectedFiles = mutableListOf<JunkFile>()
        categories.forEach { category ->
            selectedFiles.addAll(category.files.filter { it.isSelected })
        }
        return selectedFiles
    }
}