package com.majestic.grand.show.sssao

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.majestic.grand.show.R
import com.majestic.grand.show.SssEnd
import com.majestic.grand.show.sssao.mvp.JunkContract
import com.majestic.grand.show.sssao.mvp.JunkPresenter
import com.majestic.grand.show.databinding.SssSaoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SssSao : AppCompatActivity(), JunkContract.View {

    private val binding by lazy { SssSaoBinding.inflate(layoutInflater) }
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var presenter: JunkContract.Presenter
    private val categories = mutableListOf<JunkCategory>()
    private var isScanning = false
    private val TAG = "SssSao"

    private var totalScannedSize = 0L
    private var totalFileCount = 0
    private var lastUpdateTime = 0L
    private var rotationAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clean)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        initPresenter()

        lifecycleScope.launch {
            delay(500)
            presenter.startScan()
        }
    }


    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            if (!isScanning) {
                finish()
            }
        }

        setupRecyclerView()

        binding.btnCleanNow.setOnClickListener {
            startCleaning()
        }

        binding.btnCleanNow.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.isIndeterminate = true

    }

    private fun setupRecyclerView() {

        initializeStandardCategories()

        categoryAdapter = CategoryAdapter(categories) {
            updateCleanButtonState()
        }

        binding.rvCategories.layoutManager = LinearLayoutManager(this)

        binding.rvCategories.adapter = categoryAdapter

        binding.rvCategories.visibility = View.VISIBLE

    }

    private fun initializeStandardCategories() {
        val standardCategoryNames = listOf("App Cache", "Apk Files", "Log Files", "Temp Files", "Other")
        categories.clear()
        standardCategoryNames.forEach { categoryName ->
            categories.add(JunkCategory(categoryName))
        }
    }

    private fun initPresenter() {
        presenter = JunkPresenter(this, this)
    }

    private fun startCleaning() {
        binding.btnCleanNow.isEnabled = false
        binding.btnCleanNow.text = "Cleaning..."

        lifecycleScope.launch {
            val selectedFiles = (presenter as JunkPresenter).getSelectedFiles()

            if (selectedFiles.isEmpty()) {
                withContext(Dispatchers.Main) {
                    showToast("No selected files need to be cleaned")
                    binding.btnCleanNow.isEnabled = true
                    binding.btnCleanNow.text = "Clean Now"
                }
                return@launch
            }

            presenter.startClean(selectedFiles)
        }
    }

    private fun updateCleanButtonState() {
        val hasSelectedFiles = categories.any { category ->
            category.files.any { it.isSelected }
        }

        binding.btnCleanNow.isEnabled = hasSelectedFiles
        binding.btnCleanNow.alpha = if (hasSelectedFiles) 1.0f else 0.5f
        binding.btnCleanNow.text = if (hasSelectedFiles) "Clean Now" else "No Files Selected"

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (!isScanning) {
            super.onBackPressed()
        } else {
            showToast("Scanning, wait...")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::categoryAdapter.isInitialized) {
            categoryAdapter.cleanup()
        }
        presenter.onDestroy()
    }
    
    // 实现JunkContract.View接口
    override fun showScanProgress(currentPath: String, scannedSize: Long) {
        totalScannedSize = scannedSize

        val sizeInMB = scannedSize / (1024.0 * 1024.0)
        if (sizeInMB < 1.0) {
            val sizeInKB = scannedSize / 1024.0
            binding.tvScannedSize.text = String.format("%.1f", sizeInKB)
            binding.tvScannedSizeUn.text = "KB"
        } else {
            binding.tvScannedSize.text = String.format("%.1f", sizeInMB)
            binding.tvScannedSizeUn.text = "MB"
        }

        val displayPath = if (currentPath.length > 50) {
            "..." + currentPath.substring(currentPath.length - 47)
        } else {
            currentPath
        }
        binding.tvScanningPath.text = "Scanning: $displayPath"
    }

    override fun addFileToCategory(junkFile: JunkFile, categoryName: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val category = categories.find { it.name == categoryName }
            if (category != null) {
                if (category.files.size < JunkCategory.MAX_FILES_PER_CATEGORY) {
                    // 文件已经在presenter中添加，这里只需要更新UI
                    val categoryIndex = categories.indexOf(category)
                    if (categoryIndex != -1) {
                        categoryAdapter.notifyItemChanged(categoryIndex)
                    }

                    updateTotalInfo(categories.sumOf { it.files.size })
                }
            }
        }
    }

    override fun showScanComplete(categories: List<JunkCategory>) {
        isScanning = false

        // 更新本地类别列表
        this.categories.clear()
        this.categories.addAll(categories)

        val totalSize = categories.sumOf { it.getTotalSize() }
        val totalFiles = categories.sumOf { it.files.size }
        val sizeInMB = totalSize / (1024.0 * 1024.0)

        categories.forEach { category ->
            Log.d(TAG, "  ${category.name}: ${category.files.size} 文件, ${category.getTotalSizeInMB()}")
        }

        binding.tvTitle.text = "Scan Complete"

        updateTotalInfo(categories.sumOf { it.files.size })

        binding.progressBar.visibility = View.GONE

        binding.btnCleanNow.visibility = View.VISIBLE

        if (totalSize > 0) {
            binding.clean.setBackgroundResource(R.drawable.bj_junk)
            binding.imgBg.setImageResource(R.drawable.bj_junk)
            binding.btnCleanNow.isEnabled = true
            binding.btnCleanNow.alpha = 1.0f
            binding.btnCleanNow.text = "Clean Now"
        } else {
            binding.tvScanningPath.text = "All categories shown - some may be empty"
            binding.btnCleanNow.isEnabled = false
            binding.btnCleanNow.alpha = 0.5f
            binding.btnCleanNow.text = "No Files to Clean"
        }

        categoryAdapter.notifyDataSetChanged()
    }

    override fun showError(error: String) {
        showToast(error)
        binding.btnCleanNow.isEnabled = true
        binding.btnCleanNow.text = "Clean Now"
    }

    override fun updateCleanProgress(progress: Int) {
        binding.btnCleanNow.text = "Cleaning... $progress%"
    }

    override fun showCleanComplete(cleanedSize: Long) {
        val intent = Intent(this, SssEnd::class.java)
        intent.putExtra("deleted_size", cleanedSize)
        startActivity(intent)
        finish()
    }

    override fun updateTotalInfo(totalFileCount: Int) {
        this.totalFileCount = totalFileCount
        val totalSize = categories.sumOf { it.getTotalSize() }
        val sizeInMB = totalSize / (1024.0 * 1024.0)

        binding.tvScanningPath.text = "Found $totalFileCount files in ${categories.count { it.files.isNotEmpty() }} categories"

        if (sizeInMB < 1.0) {
            val sizeInKB = totalSize / 1024.0
            binding.tvScannedSize.text = String.format("%.1f", if (sizeInKB < 0.1) 0.1 else sizeInKB)
            binding.tvScannedSizeUn.text = "KB"
        } else {
            binding.tvScannedSize.text = String.format("%.1f", sizeInMB)
            binding.tvScannedSizeUn.text = "MB"
        }
    }
}