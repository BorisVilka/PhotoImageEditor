package com.edit.photo.foto.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.edit.photo.foto.databinding.SaveDialogBinding

class SaveDialog(val call1: ()->Unit, val call2: () -> Unit): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var adb = AlertDialog.Builder(requireContext())
        val binding = SaveDialogBinding.inflate(layoutInflater)
        binding.imageView8.setOnClickListener {
            call1()
            dismiss()
        }
        binding.imageView9.setOnClickListener {
            call2()
            dismiss()
        }
        adb = adb.setView(binding.root)
        val dialog = adb.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}