package com.majestic.grand.show

import android.Manifest
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.majestic.grand.show.databinding.ItemPhotoDateBinding
import com.majestic.grand.show.databinding.ItemPhotoImgBinding
import com.majestic.grand.show.databinding.SssImBinding
import com.majestic.grand.show.databinding.SssLoadBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SssIm : AppCompatActivity() {
    private val binding by lazy { SssImBinding.inflate(layoutInflater) }
    private val bindingLoad by lazy { SssLoadBinding.inflate(layoutInflater) }

    // 数据结构
    data class PhotoItem(
        val id: Long,
        val uri: Uri,
        val path: String,
        val size: Long,
        var isSelected: Boolean = false
    )

    data class PhotoDateGroup(
        val date: String,
        val dateFormatted: String,
        val photos: ArrayList<PhotoItem>,
        var isSelected: Boolean = false
    )

    private val photoGroups = ArrayList<PhotoDateGroup>()
    private val dateAdapter = PhotoDateAdapter()
    private var totalSelectedSize: Long = 0L
    private var isGlobalSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 先显示加载页面
        setContentView(bindingLoad.root)

        // 启动加载动画
        animateLoading()

        // 1秒后切换到主页面并加载照片
        Handler(Looper.getMainLooper()).postDelayed({ 
            setContentView(binding.root)



            // 为根布局设置insets监听器
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.photo)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }



            this.supportActionBar?.hide()

            // 初始化RecyclerView
            binding.rvCategories.layoutManager = GridLayoutManager(this, 1)
            binding.rvCategories.adapter = dateAdapter

            // 加载照片
            loadPhotos()

            // 设置全选按钮点击事件
            binding.cbSelectAllGlobal.setOnClickListener { view ->
                toggleGlobalSelection()
            }
            binding.imageView3.setOnClickListener {
                finish()
            }
            // 设置删除按钮点击事件
            binding.btnCleanNow.setOnClickListener { view ->
                showDeleteConfirmationDialog()
            }
        }, 1000)
    }

    // 启动加载动画
    private fun animateLoading() {
        bindingLoad.root.setOnClickListener { }
        bindingLoad.imgBack.setOnClickListener {
            finish()
        }
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = RotateAnimation.INFINITE
        rotateAnimation.interpolator = android.view.animation.LinearInterpolator()
        bindingLoad.load1.startAnimation(rotateAnimation)
    }

    // 加载照片
    private fun loadPhotos() {
        // 使用ContentResolver获取所有图片
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED
        )

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            // 按日期分组
            val dateMap = HashMap<String, PhotoDateGroup>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateColumn) * 1000 // 转换为毫秒

                // 检查文件是否存在
                if (!File(path).exists()) continue

                // 格式化日期
                val date = dateFormat.format(Date(dateAdded))
                val displayDate = displayFormat.format(Date(dateAdded))

                // 创建照片项
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val photo = PhotoItem(id, uri, path, size)

                // 按日期分组
                if (!dateMap.containsKey(date)) {
                    dateMap[date] = PhotoDateGroup(date, displayDate, ArrayList())
                }
                dateMap[date]?.photos?.add(photo)
            }

            // 转换为列表并排序
            photoGroups.addAll(dateMap.values.sortedByDescending { group -> group.date })
            dateAdapter.notifyDataSetChanged()
            updateSelectedSize()
        }
    }

    // 切换全局选择状态
    private fun toggleGlobalSelection() {
        isGlobalSelected = !isGlobalSelected
        binding.cbSelectAllGlobal.setImageResource(if (isGlobalSelected) R.drawable.check else R.drawable.discheck2)

        // 更新所有组和照片的选择状态
        photoGroups.forEach { group ->
            group.isSelected = isGlobalSelected
            group.photos.forEach { photo ->
                photo.isSelected = isGlobalSelected
            }
        }

        dateAdapter.notifyDataSetChanged()
        updateSelectedSize()
    }

    // 切换日期组选择状态
    private fun toggleDateGroupSelection(position: Int) {
        val group = photoGroups[position]
        group.isSelected = !group.isSelected

        // 更新该组下所有照片的选择状态
        group.photos.forEach { photo ->
            photo.isSelected = group.isSelected
        }

        // 更新全局选择状态
        updateGlobalSelectionStatus()

        // 刷新适配器
        dateAdapter.notifyItemChanged(position)
        updateSelectedSize()
    }

    // 切换照片选择状态
    private fun togglePhotoSelection(groupPosition: Int, photoPosition: Int) {
        val photo = photoGroups[groupPosition].photos[photoPosition]
        photo.isSelected = !photo.isSelected

        // 检查该组是否全部选中或取消选中
        updateDateGroupSelectionStatus(groupPosition)

        // 更新全局选择状态
        updateGlobalSelectionStatus()

        // 刷新适配器
        dateAdapter.notifyItemChanged(groupPosition)
        updateSelectedSize()
    }

    // 更新日期组选择状态
    private fun updateDateGroupSelectionStatus(groupPosition: Int) {
        val group = photoGroups[groupPosition]
        val allSelected = group.photos.all { photo -> photo.isSelected }
        val noneSelected = group.photos.none { photo -> photo.isSelected }

        if (allSelected) {
            group.isSelected = true
        } else if (noneSelected) {
            group.isSelected = false
        }
    }

    // 更新全局选择状态
    private fun updateGlobalSelectionStatus() {
        val allSelected = photoGroups.all { group -> group.isSelected }
        val noneSelected =
            photoGroups.all { group -> group.photos.none { photo -> photo.isSelected } }

        if (allSelected) {
            isGlobalSelected = true
            binding.cbSelectAllGlobal.setImageResource(R.drawable.check)
        } else if (noneSelected) {
            isGlobalSelected = false
            binding.cbSelectAllGlobal.setImageResource(R.drawable.discheck2)
        }
    }

    // 更新选中的大小
    private fun updateSelectedSize() {
        totalSelectedSize = 0
        photoGroups.forEach { group ->
            group.photos.forEach { photo ->
                if (photo.isSelected) {
                    totalSelectedSize += photo.size
                }
            }
        }

        // 格式化大小
        val (size, unit) = formatSize(totalSelectedSize)
        binding.tvSize.text = size
        binding.tvSizeUn.text = unit
    }

    // 格式化大小
    private fun formatSize(bytes: Long): Pair<String, String> {
        return when {
            bytes < 1024 -> Pair("$bytes", " B")
            bytes < 1024 * 1024 -> Pair(String.format("%.1f", bytes / 1024.0), " KB")
            bytes < 1024 * 1024 * 1024 -> Pair(
                String.format("%.1f", bytes / (1024.0 * 1024.0)),
                " MB"
            )

            else -> Pair(String.format("%.1f", bytes / (1024.0 * 1024.0 * 1024.0)), " GB")
        }
    }

    // 显示删除确认对话框
    private fun showDeleteConfirmationDialog() {
        if (totalSelectedSize <= 0) {
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete the selected photos?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteSelectedPhotos()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 删除选中的照片
    private fun deleteSelectedPhotos() {
        // 收集需要删除的照片
        val photosToDelete = ArrayList<PhotoItem>()
        photoGroups.forEach { group ->
            photosToDelete.addAll(group.photos.filter { photo -> photo.isSelected })
        }

        // 跳转到SssEnd页面，并传递删除的文件大小
        val intent = Intent(this, SssEnd::class.java)
        intent.putExtra("deleted_size", totalSelectedSize)
        startActivity(intent)
        finish()
    }

    // 日期分类适配器
    inner class PhotoDateAdapter : RecyclerView.Adapter<PhotoDateAdapter.DateViewHolder>() {

        inner class DateViewHolder(val binding: ItemPhotoDateBinding) :
            RecyclerView.ViewHolder(binding.root) {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
            val binding =
                ItemPhotoDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DateViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
            val group = photoGroups[position]
            holder.binding.tvDate.text = group.dateFormatted

            // 设置日期选择图标
            holder.binding.imgDateSelect.setImageResource(if (group.isSelected) R.drawable.check else R.drawable.discheck2)

            // 设置日期选择点击事件
            holder.binding.imgDateSelect.setOnClickListener { view ->
                toggleDateGroupSelection(position)
            }

            // 设置内部RecyclerView
            holder.binding.rvItemFile.layoutManager = GridLayoutManager(this@SssIm, 3)
            holder.binding.rvItemFile.adapter = PhotoAdapter(position)
        }

        override fun getItemCount(): Int {
            return photoGroups.size
        }
    }

    // 照片适配器
    inner class PhotoAdapter(private val groupPosition: Int) :
        RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

        inner class PhotoViewHolder(val binding: ItemPhotoImgBinding) :
            RecyclerView.ViewHolder(binding.root) {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val binding =
                ItemPhotoImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PhotoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photo = photoGroups[groupPosition].photos[position]

            // 设置图片
            holder.binding.imgData.setImageURI(photo.uri)

            // 设置选择图标
            holder.binding.imgSelect.setImageResource(if (photo.isSelected) R.drawable.check else R.drawable.discheck2)

            // 设置点击事件
            holder.binding.imgData.setOnClickListener { view ->
                togglePhotoSelection(groupPosition, position)
            }

            holder.binding.imgSelect.setOnClickListener { view ->
                togglePhotoSelection(groupPosition, position)
            }
        }

        override fun getItemCount(): Int {
            return photoGroups[groupPosition].photos.size
        }
    }
}