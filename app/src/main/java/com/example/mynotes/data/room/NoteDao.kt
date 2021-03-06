package com.example.mynotes.data.room

import androidx.room.*
import com.example.mynotes.data.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    suspend fun getAllNotes() : List<Note>

    @Query("SELECT * FROM notes WHERE id =:id")
    suspend fun getSpecificNote(id:Int) : Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(note:Note)

    @Delete
    suspend fun deleteNote(note:Note)

    @Query("DELETE FROM notes WHERE id =:id")
    suspend fun deleteSpecificNote(id:Int)

    @Update
    suspend fun updateNote(note:Note)


}