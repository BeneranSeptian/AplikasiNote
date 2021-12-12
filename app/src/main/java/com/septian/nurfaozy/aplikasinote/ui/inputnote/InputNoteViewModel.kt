package com.septian.nurfaozy.aplikasinote.ui.inputnote

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.septian.nurfaozy.aplikasinote.data.Note
import com.septian.nurfaozy.aplikasinote.data.NoteDb
import com.septian.nurfaozy.aplikasinote.models.EndLocation
import com.septian.nurfaozy.aplikasinote.models.StartLocation
import com.septian.nurfaozy.aplikasinote.network.MapDataSource
import com.septian.nurfaozy.aplikasinote.network.Result
import com.septian.nurfaozy.aplikasinote.repository.NoteRepository
import com.septian.nurfaozy.aplikasinote.repository.NoteRepositoryImplentation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class InputNoteViewModel(application: Application) : AndroidViewModel(application)  {
    private val NoteRepository : NoteRepository
    private val _inputState = MutableStateFlow(InputNoteState())
    val noteState : StateFlow<InputNoteState> = _inputState

    private val _eventFlow = MutableSharedFlow<InputNoteEffect>()
    val eventFlow = _eventFlow.asSharedFlow()
    init {
        val noteDao = NoteDb.getDatabase(application).noteDao
        NoteRepository = NoteRepositoryImplentation(noteDao, MapDataSource())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun OnAction(action:InputNoteAction){
        when (action){
            is InputNoteAction.EnterContent -> TODO()
            is InputNoteAction.EnterDate -> TODO()
            is InputNoteAction.EnterLatLong -> getDirections(action.startLocation, action.endLocation)
            is InputNoteAction.EnterTime -> TODO()
            is InputNoteAction.EnterTitle -> TODO()


            is InputNoteAction.SaveNote -> {

                insertNote(action.title,action.content,action.location,action.date,action.time)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNote(title:String,
                   content: String,
                   location:LatLng,
    tanggal :String, jam :String)
    {
        val rencanaJanjiannnString = "$tanggal $jam"
        val displayFormat= DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val date: LocalDateTime = LocalDateTime.parse(rencanaJanjiannnString,displayFormat)
        val note = Note(
            title=title,
            content = content,
            completed = false,
            favourite = false,
            latitude = location.latitude,
            longitude = location.longitude,
            planAction = date.atZone(ZoneOffset.UTC).toInstant().epochSecond,
            created = System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                NoteRepository.insertNote(note)
                _eventFlow.emit(InputNoteEffect.SaveNoteSuccess)

            }catch (ex: Exception){
                Log.d("Error :",ex.message.orEmpty())
                _eventFlow.emit(InputNoteEffect.SaveNoteFailed(ex.message.orEmpty()))
            }
        }
    }

    private fun getDirections(startLocation: LatLng, endLocation: LatLng){
        viewModelScope.launch{
            val start = "${startLocation.latitude}, ${startLocation.longitude}"
            val end = "${endLocation.latitude}, ${endLocation.longitude}"
            val result = NoteRepository.getDirection(
                start = start, end = end, apiKey = "AIzaSyAi70ABeq8v8fxA3FLBu1H7Dq4T8XyOO_Q"
            )
           result.collect { directionResponse ->
               if(directionResponse.status == Result.Status.SUCCESS){
                   val shape = directionResponse.data?.routes?.get(0)?.overviewPolyline?.points.orEmpty()
                   _eventFlow.emit(
                       InputNoteEffect.DrawPolyLine(endLocation, shape)
                   )
               }
           }
        }
    }
}