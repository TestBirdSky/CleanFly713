package com.majestic.grand.show.sssf.lan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.majestic.grand.show.R
import com.majestic.grand.show.databinding.TmFileBinding


class FileAdapter(
    private var fileList: List<FileItem>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    
    interface OnItemClickListener {
        fun onItemClick(fileItem: FileItem, position: Int)
    }

    inner class FileViewHolder(private val binding: TmFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fileItem: FileItem) {
            binding.tvFileName.text = fileItem.name
            binding.tvFileSize.text = fileItem.getFormattedSize()
            
            // 根据选中状态设置图片
            if (fileItem.isSelected) {
                binding.imageView.setImageResource(R.drawable.check)
            } else {
                binding.imageView.setImageResource(R.drawable.discheck2)
            }
            
            // 点击事件
            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(fileItem, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = TmFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(fileList[position])
    }

    override fun getItemCount(): Int {
        return fileList.size
    }
    
    // 更新数据
    fun updateData(newFileList: List<FileItem>) {
        fileList = newFileList
        notifyDataSetChanged()
    }
    
    // 选中或取消选中单个文件
    fun toggleSelection(position: Int) {
        if (position in 0 until fileList.size) {
            val fileItem = fileList[position]
            fileItem.isSelected = !fileItem.isSelected
            notifyItemChanged(position)
        }
    }
    
    // 选中所有文件
    fun selectAll() {
        fileList.forEach { it.isSelected = true }
        notifyDataSetChanged()
    }
    
    // 取消选中所有文件
    fun deselectAll() {
        fileList.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }
    
    // 获取选中的文件列表
    fun getSelectedFiles(): List<FileItem> {
        return fileList.filter { it.isSelected }
    }
    
    // 获取选中文件的总大小
    fun getSelectedFilesSize(): Long {
        return fileList.filter { it.isSelected }.sumOf { it.size }
    }
}