package com.septian.nurfaozy.aplikasinote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val title:String,
    val content: String,
    val completed: Boolean,
    val favourite: Boolean,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "plan_action")
    val planAction: Long = System.currentTimeMillis(),
    val created: Long = System.currentTimeMillis()
)