package com.septian.nurfaozy.aplikasinote.ui.home

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.septian.nurfaozy.aplikasinote.data.Note
import com.septian.nurfaozy.aplikasinote.data.NoteDb
import com.septian.nurfaozy.aplikasinote.network.MapDataSource
import com.septian.nurfaozy.aplikasinote.repository.NoteRepository
import com.septian.nurfaozy.aplikasinote.repository.NoteRepositoryImplentation
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val NoteRepository : NoteRepository
        init {
            val noteDao = NoteDb.getDatabase(application).noteDao
            NoteRepository = NoteRepositoryImplentation(noteDao, MapDataSource())
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNote(){
        val rencanaJanjiannnString = "2021-11-20 13:00"
        val displayFormat=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val date:LocalDateTime = LocalDateTime.parse(rencanaJanjiannnString,displayFormat)
        val note = Note(
            title="janjian dengan yajid",
            content = "main bareng di taman menteng",
            completed = false,
            favourite = false,
            latitude = -6.198890,
            longitude = 106.844460,
            planAction = date.atZone(ZoneOffset.UTC).toInstant().epochSecond,
            created = System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
              NoteRepository.insertNote(note)
            }catch (ex:Exception){
                Log.d("Error :",ex.message.orEmpty())
            }
        }
    }
}