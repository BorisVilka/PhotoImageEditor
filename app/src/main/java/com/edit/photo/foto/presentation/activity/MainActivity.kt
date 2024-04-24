package com.edit.photo.foto.presentation.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edit.photo.foto.databinding.ActivityMainBinding
import com.edit.photo.foto.presentation.adapter.ProjectsAdapter
import com.edit.photo.foto.presentation.app.App
import com.edit.photo.foto.presentation.viewmodel.MainViewModel
import com.edit.photo.foto.presentation.viewmodel.MainViewModelFactory
import com.google.gson.Gson
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ProjectsAdapter

    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var captureImage: ActivityResultLauncher<Uri>

    private lateinit var requestCameraPermission: ActivityResultLauncher<String>

    private lateinit var editActivityResultContracts: ActivityResultLauncher<Intent>

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).getAppComponent().inject(this)
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editActivityResultContracts = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode== RESULT_OK) {
                Log.d("TAG","OK")
                val data = result.data!!
                val idProject = data.getIntExtra("idProject",-1)
                Log.d("TAG",data.getBooleanExtra("isModified",false).toString())
                Log.d("TAG",idProject.toString())
                if(data.getBooleanExtra("isModified",false)) {
                    var ind = 0
                    for(i in adapter.data.indices) {
                        if(adapter.data[i].id==idProject) {
                            ind = i
                            break
                        }
                    }
                    adapter.data[ind] = viewModel.getProject(idProject)
                    adapter.notifyItemChanged(ind)
                } else {
                    adapter.data.add(viewModel.getProject(idProject))
                    adapter.notifyItemInserted(adapter.data.size-1)
                }
            } else {
                Log.d("TAG","CANCELED")
            }
        }
        requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                uri = makeUriToPhoto()
                captureImage.launch(uri)
            }
        }
        captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if(it) {
                openNewProject(uri)
            }
        }
        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> openNewProject(uri) }
        binding.imageView14.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.imageView13.setOnClickListener {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
       /* if(Build.VERSION.SDK_INT>=30) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
        }*/
        adapter = ProjectsAdapter(viewModel.getList(),this) {
            val intent1 = Intent(this@MainActivity, EditActivity::class.java)
            intent1.putExtra("id",it.id)
            editActivityResultContracts.launch(intent1)
        }
        registerForContextMenu(binding.grid)
        binding.grid.adapter = adapter
    }

    private fun makeUriToPhoto(): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "tmpFile")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }

    private fun openNewProject(uri: Uri?) {
        val intent1 = Intent(this@MainActivity, EditActivity::class.java)
        val project = com.themanufacturers.domain.model.ImageProject()
        project.imageUri = uri.toString()
        project.id = viewModel.getNewProjectId()
        intent1.putExtra("project", Gson().toJson(project))
        editActivityResultContracts.launch(intent1)
    }

    override fun onDestroy() {
        editActivityResultContracts.unregister()
        requestCameraPermission.unregister()
        captureImage.unregister()
        getImage.unregister()
        super.onDestroy()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.getPosition()
        val removedProject = adapter.data.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position,adapter.data.size-position)
        viewModel.deleteProject(removedProject)
        return super.onContextItemSelected(item)
    }

}