package com.edit.photo.foto.presentation.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.text.Selection
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edit.photo.foto.R
import com.edit.photo.foto.databinding.ActivityEditBinding
import com.edit.photo.foto.presentation.EditView
import com.edit.photo.foto.presentation.SaveDialog
import com.edit.photo.foto.presentation.adapter.ColorsAdapter
import com.edit.photo.foto.presentation.adapter.StickersAdapter
import com.edit.photo.foto.presentation.app.App
import com.edit.photo.foto.presentation.viewmodel.EditViewModel
import com.edit.photo.foto.presentation.viewmodel.EditViewModelFactory
import com.google.gson.Gson
import com.themanufacturers.domain.model.ImageProject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min


class EditActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: EditViewModelFactory

    private lateinit var viewModel: EditViewModel
    private lateinit var binding: ActivityEditBinding

    private var clearText = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).getAppComponent().inject(this)
        viewModel = ViewModelProvider(this,viewModelFactory)[EditViewModel::class.java]
        binding = ActivityEditBinding.inflate(layoutInflater)
        setResult(RESULT_CANCELED)
        val colors = mutableListOf(
            R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7,
            R.color.white, R.color.black
        )
        binding.colors.adapter = ColorsAdapter(colors,this) {
            binding.edit.project.texts[binding.edit.textInd].color = String.format(
                "#%06X",
                0xFFFFFF and getColor(it)
            )
        }
        binding.edit.project = if(intent.getIntExtra("id",-1)!=-1) viewModel.getItemById(intent.getIntExtra("id",-1))
                else Gson().fromJson(intent.getStringExtra("project"), ImageProject::class.java)
        binding.back.setOnClickListener {
            finish()
        }
        binding.imageView10.setOnClickListener {
            if(binding.edit.ind!=3) {
                binding.stickers.visibility = View.INVISIBLE
                lifecycleScope.launch {
                    clearText = true
                    binding.editText.setText("")
                    binding.editText.requestFocus()
                }
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
                binding.edit.addText()
            } else {
                binding.edit.cleanTextFocus()
            }
        }
        binding.editText.addTextChangedListener {
            if(binding.edit.textInd!=-1 && !clearText) binding.edit.project.texts[binding.edit.textInd].text = it.toString()
            else clearText = false
        }
        val openTree = registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
            Log.d("TAG",uri.toString())
            if(uri!=null) {
               lifecycleScope.launch {
                   delay(500)
                   binding.edit.saveToUri(uri) {
                       save(it)
                       MediaScannerConnection.scanFile(this@EditActivity, arrayOf(uri.path.toString()),null,null)
                   }
               }
            }
        }
        binding.save.setOnClickListener {
            val dialog = SaveDialog({
                binding.edit.share {
                    save(it)
                }
            },{
                openTree.launch("$packageName${binding.edit.project.id}")
            })
            dialog.show(supportFragmentManager,"SAVE")
        }
        binding.edit.setSelectionCallback(object  : EditView.Companion.SelectionCallback {
            override fun invalidate(ind: Int) {
                lifecycleScope.launch { this@EditActivity.invalidate(ind) }
            }
        })
        binding.edit.setTextCallback(object : EditView.Companion.TextCallback {
            override fun open() {
                Log.d("TAG","OPEN TEXT")
                lifecycleScope.launch {
                    clearText = true
                    binding.editText.setText(binding.edit.project.texts[binding.edit.textInd].text)
                    val position = binding.editText.text!!.length
                    Selection.setSelection(binding.editText.text,position)
                    binding.editText.requestFocus()
                }
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
            }
        })
        binding.edit.setResizeCallback(object : EditView.Companion.ResizeCallback {
            override fun resize(b: Bitmap,small: Boolean) {
               lifecycleScope.launch {
                   val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                       FrameLayout.LayoutParams.WRAP_CONTENT)
                   if(small) {
                       params.height = min(b.height,binding.edit.height)
                       params.width = min(b.width,binding.edit.width)
                   } else {
                       params.height = max(b.height,binding.edit.height)
                       params.width = max(b.width,binding.edit.width)
                   }
                   params.gravity = (binding.edit.layoutParams as FrameLayout.LayoutParams).gravity
                   binding.layout.updateViewLayout(binding.edit,params)
               }
            }

        })
        binding.imageView4.setOnClickListener {
            binding.edit.enableCutMode()
            //invalidate(binding.edit.ind)
            binding.edit.cleanTextFocus()
        }
        binding.stickers.adapter = StickersAdapter {
            binding.edit.addSticker(it)
            binding.stickers.visibility = View.INVISIBLE
        }
        binding.imageView5.setOnClickListener {
            if(binding.stickers.visibility== View.VISIBLE) {
                binding.stickers.visibility = View.INVISIBLE
            } else {
                binding.edit.cleanTextFocus()
                binding.stickers.visibility = View.VISIBLE
            }
        }
        setContentView(binding.root)
    }

    private var discardChanges = false

    private fun projectIsModified(project: ImageProject) {
        if(!discardChanges) {
            val data = Intent()
            data.putExtra("isModified",true)
            data.putExtra("idProject",project.id)
            setResult(RESULT_OK,data)
            discardChanges = true
        }
    }

    private fun projectIsAdded(project: ImageProject) {
        val data = Intent()
        data.putExtra("isModified",false)
        data.putExtra("idProject",project.id)
        setResult(RESULT_OK,data)
        discardChanges = true
    }

    private fun invalidate(ind: Int) {
        binding.imageView4.setBackgroundResource(if(ind==1) R.drawable.border else android.R.color.transparent)
        binding.imageView5.setBackgroundResource(if(ind==2) R.drawable.border else android.R.color.transparent)
        binding.imageView10.setBackgroundResource(if(ind==3) R.drawable.border else android.R.color.transparent)
        if(ind==3) {
            binding.colors.visibility = View.VISIBLE
        } else binding.colors.visibility = View.INVISIBLE
    }

    override fun onResume() {
        binding.edit.paused = false
        super.onResume()
    }
    private fun save(it: ImageProject) {
        if(viewModel.projectExist(it.id)) {
            viewModel.modifyProject(it)
            projectIsModified(it)
        } else {
            viewModel.addProject(it)
            projectIsAdded(it)
        }
    }
}