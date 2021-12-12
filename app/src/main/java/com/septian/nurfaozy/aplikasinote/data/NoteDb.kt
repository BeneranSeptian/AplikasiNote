package com.septian.nurfaozy.aplikasinote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    entities = [Note::class], version =1 )
abstract class NoteDb : RoomDatabase(){
    abstract val noteDao:NoteDao
    companion object {
        private const val DATABASE_NAME = "database_note"
        @Volatile
        private var INSTANCE: NoteDb?=null
        fun getDatabase(context: Context):NoteDb{
            val tempInstance= INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,NoteDb::class.java, DATABASE_NAME
                ).build()
                INSTANCE=instance
                return  instance

            }
        }
    }
}