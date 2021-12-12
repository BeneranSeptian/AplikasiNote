package com.septian.nurfaozy.aplikasinote.repository

import androidx.room.FtsOptions
import com.septian.nurfaozy.aplikasinote.data.Note
import com.septian.nurfaozy.aplikasinote.data.NoteDao
import com.septian.nurfaozy.aplikasinote.models.DirectionResponses
import com.septian.nurfaozy.aplikasinote.network.MapDataSource
import com.septian.nurfaozy.aplikasinote.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.Dispatcher

class NoteRepositoryImplentation(private val dao:NoteDao, private val mapDataSource: MapDataSource):NoteRepository {

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun getById(id: Int): Note? {
        return dao.getNoteById(id)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

    override fun GetNotes(noteOrder: NoteOrder): Flow<List<Note>> {
        return dao.getNotes().map {notes->
            when(noteOrder.orderType){
                is OrderType.Ascending ->{
                    when(noteOrder){
                        is NoteOrder.PlanAction -> notes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Title -> notes.sortedBy { it.planAction }
                    }
                }
                is OrderType.Descending ->{
                    when(noteOrder){
                        is NoteOrder.PlanAction -> notes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Title -> notes.sortedBy { it.planAction }
                    }

                }
            }

        }
    }

    override suspend fun getDirection(
        start: String,
        end: String,
        apiKey: String
    ): Flow<Result<DirectionResponses>> {
        return flow{
            emit(Result.loading())
            val result = mapDataSource.getDirection(
                origin = start, destination = end, apiKey = apiKey
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


}