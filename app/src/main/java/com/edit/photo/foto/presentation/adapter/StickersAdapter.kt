package com.edit.photo.foto.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edit.photo.foto.R
import com.edit.photo.foto.databinding.StickerItemBinding

class StickersAdapter(val callback: (Int)-> Unit): RecyclerView.Adapter<StickersAdapter.Companion.StickerHolder>() {

    val data = arrayListOf(
        R.drawable.t1,
        R.drawable.t2,
        R.drawable.t3,
        R.drawable.t4,
        R.drawable.t5,
        R.drawable.t6,
        R.drawable.t7,
        R.drawable.t8,
        R.drawable.t9,
        R.drawable.t10,
        R.drawable.t11,
        R.drawable.t12,
        R.drawable.t13,
        R.drawable.t14,
        R.drawable.t15,
        R.drawable.t16,
    )

    companion object {
        class StickerHolder(val binding: StickerItemBinding): ViewHolder(binding.root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerHolder {
        return StickerHolder(StickerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewRecycled(holder: StickerHolder) {
        holder.binding.root.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: StickerHolder, position: Int) {
        holder.binding.imageView6.setImageResource(data[position])
        holder.binding.root.setOnClickListener { callback(position) }
    }
}