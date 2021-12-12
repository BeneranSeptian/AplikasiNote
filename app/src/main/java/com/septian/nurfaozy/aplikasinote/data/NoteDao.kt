package com.septian.nurfaozy.aplikasinote.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id= :id")
    fun getNoteById(id:Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:Note)

    @Delete
    suspend fun deleteNote(Note:Note)

    @Update
    suspend fun updateNote(note: Note)
}