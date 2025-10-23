package com.majestic.grand.show

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.majestic.grand.show.databinding.SssWyBinding
import com.majestic.grand.show.sssao.SssSao
import com.majestic.grand.show.sssf.lan.SssFl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class SssCup : AppCompatActivity() {
    private val binding by lazy { SssWyBinding.inflate(layoutInflater) }

    private var permissionRequestSource: String? = null

    companion object {
        private const val REQUEST_SOURCE_BOOST = "boost"
        private const val REQUEST_SOURCE_PHOTO = "photo"
        private const val REQUEST_SOURCE_FOLDER = "folder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.supportActionBar?.hide()
        setupUI()
        loadStorageInfo()
    }



    private fun setupUI() {
        binding.llSao.setOnClickListener {
            permissionRequestSource = REQUEST_SOURCE_BOOST
            checkPermissionAndStartScan()
        }

        binding.quanMain.tvYes.setOnClickListener {
            hidePermissionDialog()
            PermissionUtils.requestStoragePermission(this)
        }

        binding.quanMain.tvCancel.setOnClickListener {
            hidePermissionDialog()
        }

        binding.llImage.setOnClickListener {
            permissionRequestSource = REQUEST_SOURCE_PHOTO
            checkPermissionAndStartPhotoActivity()
        }

        binding.llFile.setOnClickListener {
            permissionRequestSource = REQUEST_SOURCE_FOLDER
            checkPermissionAndStartFileActivity()
        }
        onBackPressedDispatcher.addCallback {

            if (binding.netPage.net.isVisible) {
                Log.e("TAG", "setupUI: 1", )
                binding.netPage.net.isVisible = false
                return@addCallback
            }
            Log.e("TAG", "setupUI: 2", )
            finish()
        }
        binding.settingsIcon.setOnClickListener {
            binding.netPage.net.isVisible = true
        }
        binding.netPage.root.setOnClickListener {
        }
        binding.netPage.imgBack.setOnClickListener {
            binding.netPage.net.isVisible = false
        }
        binding.netPage.tvShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${this.packageName}"
            )
            try {
                startActivity(Intent.createChooser(intent, "Share via"))
            } catch (ex: Exception) {
                Toast.makeText(this, "Failed to share", Toast.LENGTH_SHORT).show()
            }
        }

        binding.netPage.tvPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            //TODO
            intent.data = Uri.parse("https://wwww.google.com/")
            startActivity(intent)
        }
    }

    private fun checkPermissionAndStartPhotoActivity() {
        if (PermissionUtils.hasStoragePermission(this)) {
            startPhotoActivity()
        } else {
            showPermissionDialog()
        }
    }

    private fun checkPermissionAndStartFileActivity() {
        if (PermissionUtils.hasStoragePermission(this)) {
            startFileActivity()
        } else {
            showPermissionDialog()
        }
    }

    private fun startPhotoActivity() {
        val intent = Intent(this, SssIm::class.java)
        startActivity(intent)
    }

    private fun startFileActivity() {
        val intent = Intent(this, SssFl::class.java)
        startActivity(intent)
    }

    private fun loadStorageInfo() {
        lifecycleScope.launch {
            try {
                val storageInfo = withContext(Dispatchers.IO) {
                    StorageUtils.getStorageInfo(this@SssCup)
                }
                binding.usedStorage.text = storageInfo.formattedUsed
                binding.tolStorage.text = "of ${storageInfo.formattedTotal} used"
                binding.progressCircle.progress = storageInfo.usagePercentage
            } catch (e: Exception) {
                e.printStackTrace()
                binding.usedStorage.text = "0"
                binding.tolStorage.text = "0"
            }
        }
    }

    private fun checkPermissionAndStartScan() {
        if (PermissionUtils.hasStoragePermission(this)) {
            startCleanActivity()
        } else {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        binding.quanMain.quanDialog.visibility = android.view.View.VISIBLE
    }

    private fun hidePermissionDialog() {
        binding.quanMain.quanDialog.visibility = android.view.View.GONE
    }

    private fun startCleanActivity() {
        val intent = Intent(this, SssSao::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionUtils.REQUEST_STORAGE_PERMISSION -> {
                if (PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    navigateToRequestedActivity()
                } else {
                    showPermissionDeniedDialog()
                }
                permissionRequestSource = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PermissionUtils.REQUEST_MANAGE_STORAGE_PERMISSION -> {
                if (PermissionUtils.hasStoragePermission(this)) {
                    navigateToRequestedActivity()
                } else {
                    showPermissionDeniedDialog()
                }
                permissionRequestSource = null
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission request")
            .setMessage("Storage permission is required to scan and clean junk files, please manually turn on the permission in the settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                PermissionUtils.openAppSettings(this)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    override fun onResume() {
        super.onResume()
        loadStorageInfo()
    }

    private fun navigateToRequestedActivity() {
        when (permissionRequestSource) {
            REQUEST_SOURCE_BOOST -> startCleanActivity()
            REQUEST_SOURCE_PHOTO -> startPhotoActivity()
            REQUEST_SOURCE_FOLDER -> startFileActivity()
        }
    }
}