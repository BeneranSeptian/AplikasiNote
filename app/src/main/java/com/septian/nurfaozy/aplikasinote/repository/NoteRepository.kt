package com.septian.nurfaozy.aplikasinote.repository

import androidx.room.FtsOptions
import com.septian.nurfaozy.aplikasinote.data.Note
import com.septian.nurfaozy.aplikasinote.network.Result
import com.septian.nurfaozy.aplikasinote.models.DirectionResponses
import kotlinx.coroutines.flow.Flow



interface NoteRepository {
    suspend fun insertNote(note: Note)

    suspend fun getById(id:Int):Note?

    suspend fun deleteNote(note:Note)

fun GetNotes(noteOrder:NoteOrder = NoteOrder.Title(OrderType.Descending)):
        Flow<List<Note>>

    suspend fun getDirection(
        start: String, end: String, apiKey: String
    ):Flow<Result<DirectionResponses>>
}