package com.example.mynotes.ui.add_fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mynotes.R
import com.example.mynotes.data.model.Note
import com.example.mynotes.data.room.NotesDataBase
import com.example.mynotes.ui.notes_fragment.NotesFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_add_notes.*
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.layout_include.*
import kotlinx.android.synthetic.main.note_item.*
import kotlinx.android.synthetic.main.note_item.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.*

class AddNotesFragment : Fragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    var selectedColor = "#171C26"
    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var noteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = requireArguments().getInt("noteId",-1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_notes, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInclude(view)

        if (noteId != -1) {
            lifecycleScope.launch{
                context?.let {
                    var list = NotesDataBase.getDatabase(requireContext()).noteDao().getSpecificNote(noteId)
                    viewIndicator.setBackgroundColor(Color.parseColor(list.color))
                    inputTitle.setText(list.title)
                    inputSubtitle.setText(list.subTitle)
                    if (list.imgPath != "") {
                        selectedImagePath = list.imgPath!!
                        imageViewNote.setImageBitmap(BitmapFactory.decodeFile(list.imgPath))
                        imageViewNote.visibility = View.VISIBLE
                    }

                }
            }
        }

        btnSaveNote.setOnClickListener {
            saveNote()
            it.hideKeyboard()
        }

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun initInclude(view: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutInclude)

        layoutInclude.findViewById<View>(R.id.layoutInclude).setOnClickListener { v: View? ->
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        imageColor1.setOnClickListener {
            selectedColor = "#333333"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewIndicator.setBackgroundColor(Color.parseColor(selectedColor))

        }
        imageColor2.setOnClickListener {
            selectedColor = "#FDBE3B"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewIndicator.setBackgroundColor(Color.parseColor(selectedColor))

        }
        imageColor3.setOnClickListener {
            selectedColor = "#FF4842"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewIndicator.setBackgroundColor(Color.parseColor(selectedColor))

        }
        imageColor4.setOnClickListener {
            selectedColor = "#bbedee"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            viewIndicator.setBackgroundColor(Color.parseColor(selectedColor))

        }
        imageColor5.setOnClickListener {
            selectedColor = "#000000"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            viewIndicator.setBackgroundColor(Color.parseColor(selectedColor))
        }
        layoutAddImage.setOnClickListener {
            readStorageTask()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveNote() {
        val date = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val setDate = date.format(Date())

        if (inputTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Title is empty", Toast.LENGTH_SHORT).show()
        } else if (inputSubtitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Subtitle is empty", Toast.LENGTH_SHORT).show()
        } else {
            var note = Note()
            note.title = inputTitle.text.toString()
            note.subTitle = inputSubtitle.text.toString()
            note.dateTime = setDate
            note.color = selectedColor
            note.imgPath = selectedImagePath
            context?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    NotesDataBase.getDatabase(it).noteDao().insertNotes(note)
                    inputTitle.setText("")
                    inputSubtitle.setText("")
                    requireActivity().supportFragmentManager.popBackStack()
                    openNoteFragment(NotesFragment.newInstance(), false)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                AddNotesFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    private fun openNoteFragment(
            newInstance: NotesFragment, b: Boolean
    ) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        if (b) {
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.fragment, newInstance).addToBackStack(javaClass.simpleName).commit()
    }

    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun readStorageTask() {
        if (hasReadStoragePerm()){
            addImage()
        }else{
            EasyPermissions.requestPermissions(requireActivity(),
                getString(R.string.storage_permission_text),
                READ_STORAGE_PERM,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @Suppress("DEPRECATION")
    private fun addImage() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
            imageViewNote.visibility = View.VISIBLE
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath:String?
        var cursor = requireActivity().contentResolver.
        query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK)
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null){
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        imageViewNote.setImageBitmap(bitmap)
                        imageViewNote.visibility = View.VISIBLE
                        val boss = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG,10, boss)
                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    }catch (e:Exception) {
                        Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,requireActivity())
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(),perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
