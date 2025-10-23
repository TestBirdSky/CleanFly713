package com.majestic.grand.show

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.majestic.grand.show.databinding.SssEndBinding
import com.majestic.grand.show.databinding.SssLoadBinding
import com.majestic.grand.show.StorageUtils
import com.majestic.grand.show.sssao.SssSao
import com.majestic.grand.show.sssf.lan.SssFl

class SssEnd : AppCompatActivity() {
    private val binding by lazy { SssEndBinding.inflate(layoutInflater) }
    private val bindingLoad by lazy { SssLoadBinding.inflate(layoutInflater) }
    
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
            
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.end)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            this.supportActionBar?.hide()
            
            // 获取并显示删除的文件大小
            displayDeletedFileSize()
            
            // 设置按钮点击事件
            setupButtonListeners()
        }, 1000)
    }
    
    // 启动加载动画
    private fun animateLoading() {
        bindingLoad.root.setOnClickListener {  }
        bindingLoad.imgBack.setOnClickListener {
            finish()
        }
        bindingLoad.tvTip.text = "Cleaning..."
        // 使用旋转动画
        bindingLoad.load1.animate()
            .rotation(360f)
            .setDuration(1000)
            .start()
    }
    
    // 设置按钮点击事件
    private fun setupButtonListeners() {
        // 图片清理按钮
        binding.mbPicClean.setOnClickListener {
            val intent = Intent(this, SssIm::class.java)
            startActivity(intent)
            finish()
        }
        
        // 文件清理按钮
        binding.mbFileClean.setOnClickListener {
            val intent = Intent(this, SssFl::class.java)
            startActivity(intent)
            finish()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        // 清理按钮
        binding.mbClean.setOnClickListener {
            val intent = Intent(this, SssSao::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    // 显示删除的文件大小
    private fun displayDeletedFileSize() {
        // 从Intent中获取删除的文件大小
        val deletedSize = intent.getLongExtra("deleted_size", 0L)
        
        // 格式化文件大小并显示
        val formattedSize = StorageUtils.formatBytes(deletedSize)
        binding.tvCleanSizeTip.text = "Saved $formattedSize space for you"
    }
}