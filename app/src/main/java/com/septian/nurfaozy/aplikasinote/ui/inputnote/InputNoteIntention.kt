package com.septian.nurfaozy.aplikasinote.ui.inputnote

import com.google.android.gms.maps.model.LatLng

data class InputNoteState(
    val title : String="",
    val content : String="",
    val date : String="",
    val time : String="",
    val latitude : Double=0.0,
    val longitude : Double=0.0,
    val enable : Boolean = false
)

sealed class InputNoteAction{
    data class EnterTitle(val title:String):InputNoteAction()
    data class EnterContent(val content:String):InputNoteAction()
    data class EnterDate(val date:String):InputNoteAction()
    data class EnterTime(val time:String):InputNoteAction()
    data class EnterLatLong(val startLocation:LatLng, val endLocation:LatLng):InputNoteAction()
    data class SaveNote(val title:String,
                        val content :String,
                        val date :String,
                        val time :String,
                        val location : LatLng): InputNoteAction()


}

sealed class InputNoteEffect{
    object SaveNoteSuccess:InputNoteEffect()
    data class SaveNoteFailed(val errorMassage:String):InputNoteEffect()
    data class DrawPolyLine(val location:LatLng, val shape : String):InputNoteEffect()
}