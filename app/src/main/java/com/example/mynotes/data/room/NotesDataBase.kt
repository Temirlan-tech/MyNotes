package com.example.mynotes.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynotes.data.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDataBase : RoomDatabase() {

    companion object {
        var notesDatabase: NotesDataBase? = null

        @Synchronized
        fun getDatabase(context: Context): NotesDataBase {
            if (notesDatabase == null) {
                notesDatabase = Room.databaseBuilder(
                        context
                        , NotesDataBase::class.java
                        , "notes.db"
                ).build()
            }
            return notesDatabase!!
        }
    }

    abstract fun noteDao():NoteDao
}