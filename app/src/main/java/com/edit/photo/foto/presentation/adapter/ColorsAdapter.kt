package com.edit.photo.foto.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edit.photo.foto.databinding.ColorItemBinding

class ColorsAdapter(val data: MutableList<Int>,val context: Context, val colorCallback: (Int) -> Unit): RecyclerView.Adapter<ColorsAdapter.ColorHolder>() {


    class ColorHolder(val binding: ColorItemBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
       return ColorHolder(ColorItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewRecycled(holder: ColorHolder) {
        holder.binding.root.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.binding.imageView11.setColorFilter(ContextCompat.getColor(context,data[position]),PorterDuff.Mode.SRC_IN)
        holder.binding.root.setOnClickListener {
            colorCallback(data[position])
        }

    }
}