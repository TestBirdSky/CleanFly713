package com.majestic.grand.show.sssf.lan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.majestic.grand.show.PermissionUtils
import com.majestic.grand.show.R
import com.majestic.grand.show.SssEnd
import com.majestic.grand.show.databinding.SssLoadBinding
import com.majestic.grand.show.databinding.SssZlBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SssFl : AppCompatActivity() {
    private val binding by lazy { SssZlBinding.inflate(layoutInflater) }
    private val bindingLoad by lazy { SssLoadBinding.inflate(layoutInflater) }

    private lateinit var fileAdapter: FileAdapter
    private var allFiles = listOf<FileItem>()
    private var filteredFiles = listOf<FileItem>()

    // 当前筛选条件
    private var currentTypeFilter = FileScanner.FileTypeFilter.ALL
    private var currentSizeFilter = FileScanner.FileSizeFilter.ALL
    private var currentTimeFilter = FileScanner.FileTimeFilter.ALL

    // 是否全选
    private var isAllSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 先显示加载页面
        setContentView(bindingLoad.root)

        // 启动加载动画
        animateLoading()

        // 1秒后切换到主页面
        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(binding.root)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.zl)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            this.supportActionBar?.hide()

            // 初始化UI
            initUI()

            // 检查权限并扫描文件
            checkPermissionAndScanFiles()
        }, 1000)
    }

    // 启动加载动画
    private fun animateLoading() {
        bindingLoad.root.setOnClickListener { }
        bindingLoad.imgBack.setOnClickListener {
            finish()
        }
        // 使用旋转动画
        bindingLoad.load1.animate()
            .rotation(360f)
            .setDuration(1000)
            .start()
    }

    // 初始化UI
    private fun initUI() {
        // 初始化RecyclerView
        binding.rvFiles.layoutManager = LinearLayoutManager(this)
        fileAdapter = FileAdapter(emptyList(), object : FileAdapter.OnItemClickListener {
            override fun onItemClick(fileItem: FileItem, position: Int) {
                // 点击文件项，切换选中状态
                fileAdapter.toggleSelection(position)
                updateDeleteButtonText()
            }
        })
        binding.rvFiles.adapter = fileAdapter

        // 设置返回按钮点击事件
        binding.imgBack.setOnClickListener {
            finish()
        }

        // 设置筛选按钮点击事件
        binding.tvType.setOnClickListener {
            showTypeFilterPopup(it)
        }

        binding.tvSize.setOnClickListener {
            showSizeFilterPopup(it)
        }

        binding.tvTime.setOnClickListener {
            showTimeFilterPopup(it)
        }

        // 设置全选按钮点击事件
        binding.tvSelectAll.setOnClickListener {
            toggleSelectAll()
        }

        // 设置删除按钮点击事件
        binding.layoutBottomDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    // 检查权限并扫描文件
    private fun checkPermissionAndScanFiles() {
        if (PermissionUtils.hasStoragePermission(this)) {
            scanFiles()
        } else {
            PermissionUtils.requestStoragePermission(this)
        }
    }

    // 扫描文件
    private fun scanFiles() {
        binding.rvFiles.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.GONE

        // 在协程中扫描文件，避免ANR
        lifecycleScope.launch {
            try {
                allFiles = FileScanner.scanAllFiles(this@SssFl)
                filterFiles()
            } catch (e: Exception) {
                e.printStackTrace()
                showEmptyState()
            }
        }
    }

    // 根据当前筛选条件过滤文件
    private fun filterFiles() {
        filteredFiles = FileScanner.filterFiles(
            allFiles,
            currentTypeFilter,
            currentSizeFilter,
            currentTimeFilter
        )

        // 更新适配器数据
        fileAdapter.updateData(filteredFiles)

        // 更新UI显示
        if (filteredFiles.isEmpty()) {
            showEmptyState()
        } else {
            showFileList()
        }

        // 重置全选状态
        isAllSelected = false
        updateSelectAllButton()
        updateDeleteButtonText()
    }

    // 显示文件列表
    private fun showFileList() {
        binding.rvFiles.visibility = View.VISIBLE
        binding.layoutEmptyState.visibility = View.GONE
    }

    // 显示空状态
    private fun showEmptyState() {
        binding.rvFiles.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.VISIBLE
    }

    // 显示文件类型筛选下拉框
    private fun showTypeFilterPopup(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.apply {
            add(0, 0, 0, "All Type")
            add(0, 1, 1, "Image")
            add(0, 2, 2, "Video")
            add(0, 3, 3, "Audio")
            add(0, 4, 4, "Docs")
            add(0, 5, 5, "Download")
            add(0, 6, 6, "Zip")
        }

        popupMenu.setOnMenuItemClickListener {
            currentTypeFilter = when (it.itemId) {
                0 -> FileScanner.FileTypeFilter.ALL
                1 -> FileScanner.FileTypeFilter.IMAGE
                2 -> FileScanner.FileTypeFilter.VIDEO
                3 -> FileScanner.FileTypeFilter.AUDIO
                4 -> FileScanner.FileTypeFilter.DOCS
                5 -> FileScanner.FileTypeFilter.DOWNLOAD
                6 -> FileScanner.FileTypeFilter.ZIP
                else -> FileScanner.FileTypeFilter.ALL
            }

            // 更新按钮文本和颜色
            binding.tvType.text = it.title
            binding.tvType.setTextColor(ContextCompat.getColor(this, R.color.blue_txt))
            binding.tvType.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.lan_san, 0
            )

            // 重置其他筛选按钮状态
            resetFilterButton(binding.tvSize)
            resetFilterButton(binding.tvTime)

            // 重新筛选文件
            filterFiles()
            true
        }

        popupMenu.show()
    }

    // 显示文件大小筛选下拉框
    private fun showSizeFilterPopup(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.apply {
            add(0, 0, 0, "All Size")
            add(0, 1, 1, ">10MB")
            add(0, 2, 2, ">20MB")
            add(0, 3, 3, ">50MB")
            add(0, 4, 4, ">100MB")
            add(0, 5, 5, ">200MB")
            add(0, 6, 6, ">500MB")
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            currentSizeFilter = when (menuItem.itemId) {
                0 -> FileScanner.FileSizeFilter.ALL
                1 -> FileScanner.FileSizeFilter.MB_10
                2 -> FileScanner.FileSizeFilter.MB_20
                3 -> FileScanner.FileSizeFilter.MB_50
                4 -> FileScanner.FileSizeFilter.MB_100
                5 -> FileScanner.FileSizeFilter.MB_200
                6 -> FileScanner.FileSizeFilter.MB_500
                else -> FileScanner.FileSizeFilter.ALL
            }

            // 更新按钮文本和颜色
            binding.tvSize.text = menuItem.title
            binding.tvSize.setTextColor(ContextCompat.getColor(this, R.color.blue_txt))
            binding.tvSize.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.lan_san, 0
            )

            // 重置其他筛选按钮状态
            resetFilterButton(binding.tvType)
            resetFilterButton(binding.tvTime)

            // 重新筛选文件
            filterFiles()
            true
        }

        popupMenu.show()
    }

    // 显示文件时间筛选下拉框
    private fun showTimeFilterPopup(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.apply {
            add(0, 0, 0, "All Time")
            add(0, 1, 1, "Within 1 day")
            add(0, 2, 2, "Within 1 week")
            add(0, 3, 3, "Within 1 month")
            add(0, 4, 4, "Within 3 month")
            add(0, 5, 5, "Within 6 month")
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            currentTimeFilter = when (menuItem.itemId) {
                0 -> FileScanner.FileTimeFilter.ALL
                1 -> FileScanner.FileTimeFilter.DAY_1
                2 -> FileScanner.FileTimeFilter.WEEK_1
                3 -> FileScanner.FileTimeFilter.MONTH_1
                4 -> FileScanner.FileTimeFilter.MONTH_3
                5 -> FileScanner.FileTimeFilter.MONTH_6
                else -> FileScanner.FileTimeFilter.ALL
            }

            // 更新按钮文本和颜色
            binding.tvTime.text = menuItem.title
            binding.tvTime.setTextColor(ContextCompat.getColor(this, R.color.blue_txt))
            binding.tvTime.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.lan_san, 0
            )

            // 重置其他筛选按钮状态
            resetFilterButton(binding.tvType)
            resetFilterButton(binding.tvSize)

            // 重新筛选文件
            filterFiles()
            true
        }

        popupMenu.show()
    }

    // 重置筛选按钮状态
    private fun resetFilterButton(textView: AppCompatTextView) {
        textView.setTextColor(resources.getColor(R.color.gray_txt))
        textView.setCompoundDrawablesWithIntrinsicBounds(
            0, 0, R.drawable.hui_san, 0
        )
    }

    // 切换全选状态
    private fun toggleSelectAll() {
        isAllSelected = !isAllSelected

        if (isAllSelected) {
            fileAdapter.selectAll()
        } else {
            fileAdapter.deselectAll()
        }

        updateSelectAllButton()
        updateDeleteButtonText()
    }

    // 更新全选按钮状态
    private fun updateSelectAllButton() {
        binding.tvSelectAll.text = if (isAllSelected) "Delete All" else "Delete"
    }

    // 更新删除按钮文本（显示选中数量）
    private fun updateDeleteButtonText() {
        val selectedCount = fileAdapter.getSelectedFiles().size
        binding.tvSelectAll.text = if (isAllSelected) {
            "Delete All"
        } else {
            "Delete $selectedCount"
        }
    }

    // 显示删除确认对话框
    private fun showDeleteConfirmationDialog() {
        val selectedFiles = fileAdapter.getSelectedFiles()
        if (selectedFiles.isEmpty()) return

        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete these ${selectedFiles.size} files?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteSelectedFiles()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 删除选中的文件
    private fun deleteSelectedFiles() {
        val selectedFiles = fileAdapter.getSelectedFiles()
        val deletedSize = fileAdapter.getSelectedFilesSize()

        // 在协程中删除文件，避免ANR
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                selectedFiles.forEach { fileItem ->
                    val file = File(fileItem.path)
                    if (file.exists() && file.isFile) {
                        file.delete()
                    }
                }

                // 从列表中移除已删除的文件
                allFiles = allFiles.filter { fileItem ->
                    !selectedFiles.any { it.id == fileItem.id }
                }

                // 在主线程更新UI
                withContext(Dispatchers.Main) {
                    filterFiles()
                    navigateToEndPage(deletedSize)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 跳转到结果页面
    private fun navigateToEndPage(deletedSize: Long) {
        val intent = Intent(this, SssEnd::class.java)
        intent.putExtra("deleted_size", deletedSize)
        startActivity(intent)
        finish()
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            scanFiles()
        } else {
            // 权限被拒绝，显示空状态
            showEmptyState()
        }
    }
}