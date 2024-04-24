package com.edit.photo.foto.presentation.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edit.photo.foto.R
import com.edit.photo.foto.databinding.ProjectItemBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.themanufacturers.domain.model.ImageProject
import java.io.File


class ProjectsAdapter(val data: MutableList<ImageProject>, val context: Activity, val callback: (ImageProject)->Unit): RecyclerView.Adapter<ProjectsAdapter.Companion.ProjectHolder>() {

    companion object {
        class ProjectHolder(val binding: ProjectItemBinding): ViewHolder(binding.root), View.OnCreateContextMenuListener {

            init {
                binding.root.setOnCreateContextMenuListener(this)
            }

            override fun onCreateContextMenu(
                menu: ContextMenu, v: View?,
                menuInfo: ContextMenuInfo?
            ) {
               menu.add(0,0,0, R.string.delete)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        return ProjectHolder(ProjectItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewRecycled(holder: ProjectHolder) {
        holder.binding.root.setOnClickListener(null)
        holder.binding.root.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }
    private var position = 0

    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
       holder.binding.root.setOnClickListener {
           Log.d("TAG","CLICK")
           callback(data[position])
       }
        holder.binding.root.setOnLongClickListener {
            setPosition(position)
            return@setOnLongClickListener false
        }
        //Log.d("TAG",data[position].preview)
        val bitmap = BitmapFactory.decodeByteArray(data[position].preview,0,data[position].preview!!.size)
        holder.binding.imageView3.setImageBitmap(bitmap)
        //Picasso.get().load(File(data[position].preview)).error(R.drawable.cancel).into(holder.binding.imageView3)
       /*if(data[position].preview.startsWith("content")) {
           Picasso.get()
               .load(Uri.parse(data[position].preview)).into(holder.binding.imageView3,object : Callback {
                   override fun onSuccess() {
                       Log.d("TAG","SUC")
                   }

                   override fun onError(e: Exception?) {
                       e!!.printStackTrace()
                   }

               })
       } else {
           Picasso.get()
               .load(File(data[position].preview)).into(holder.binding.imageView3,object : Callback {
                   override fun onSuccess() {
                       Log.d("TAG","SUC")
                   }

                   override fun onError(e: Exception?) {
                       e!!.printStackTrace()
                   }

               })
       }*/
    }
}