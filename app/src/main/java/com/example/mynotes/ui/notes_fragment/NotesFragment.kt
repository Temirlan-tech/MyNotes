package com.example.mynotes.ui.notes_fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.R
import com.example.mynotes.data.model.Note
import com.example.mynotes.data.room.NotesDataBase
import com.example.mynotes.ui.add_fragment.AddNotesFragment
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.ArrayList

class NotesFragment : Fragment() {

    var arrNotes = ArrayList<Note>()
    var notesAdapter: NotesAdapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesAdapter = NotesAdapter()

        btnCreateNote.setOnClickListener {
            openAddFragment(AddNotesFragment.newInstance(), false)
        }
        initRecyclerView()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var searchNote = ArrayList<Note>()
                for (arr in arrNotes){
                    if (arr.title!!.toLowerCase(Locale.getDefault()).contains(newText.toString()))
                        searchNote.add(arr)
                }
                notesAdapter.setData(searchNote)
                notesAdapter.notifyDataSetChanged()
                searchView.isIconified = false
                return true
            }
        })
    }

    private fun initRecyclerView() {
        rv_notes.setHasFixedSize(true)
        rv_notes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        viewLifecycleOwner.lifecycleScope.async {
            context?.let {
                val list: List<Note> =
                    NotesDataBase.getDatabase(requireContext()).noteDao().getAllNotes()
                notesAdapter.setData(list)
                arrNotes = list as ArrayList<Note>
                rv_notes?.adapter = notesAdapter
            }
        }
        notesAdapter!!.setOnClickListener(clickItem)
    }

    private val clickItem = object : NotesAdapter.OnItemClickListener {
        override fun onClick(notes: Int) {
            var fragment: Fragment
            var bundle = Bundle()
            bundle.putInt("noteId", notes)
            fragment = AddNotesFragment.newInstance()
            fragment.arguments = bundle
            openAddFragment(fragment, false)
        }

        override fun onLongClick(notes: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete this note?")
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                }
                .setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                    viewLifecycleOwner.lifecycleScope.async {
                        context?.let {
                            NotesDataBase.getDatabase(requireContext()).noteDao()
                                .deleteNote(arrNotes[notes])
                            notesAdapter.delete(notes)
                        }
                    }
                }
            builder.create()
            builder.show()
        }
    }

    private fun openAddFragment (newInstance: AddNotesFragment, b: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        if (b) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }
        fragmentTransition.replace(R.id.fragment, newInstance).addToBackStack(javaClass.simpleName)
            .commit()
    }

    companion object {
        fun newInstance() =
            NotesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}