package com.example.mynotes.ui.notes_fragment

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.data.model.Note
import kotlinx.android.synthetic.main.note_item.view.*

class NotesAdapter() :
        RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var listener : OnItemClickListener? = null
    var arrList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Note>) {
        arrList = arrNotesList as ArrayList<Note>
    }

    fun delete(position: Int){
        arrList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        holder.itemView.tv_title.text = arrList[position].title
        holder.itemView.tv_subTitle.text = arrList[position].subTitle
        holder.itemView.tv_dateTime.text = arrList[position].dateTime

        if (arrList[position].color != null) {
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        } else {
        }

        if (arrList[position].imgPath != null) {
            holder.itemView.iv_note.setImageBitmap(BitmapFactory.decodeFile(arrList[position].imgPath))
            holder.itemView.iv_note.visibility = View.VISIBLE
        } else {
            holder.itemView.iv_note.visibility = View.GONE
        }

        holder.itemView.cardView.setOnClickListener {
            listener!!.onClick(arrList[position].id!!)
        }

        holder.itemView.cardView.setOnLongClickListener {
            listener!!.onLongClick(arrList[position].id!!)
            return@setOnLongClickListener true
        }
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    interface OnItemClickListener {
        fun onClick(notes : Int)
        fun onLongClick(notes: Int)
    }

}