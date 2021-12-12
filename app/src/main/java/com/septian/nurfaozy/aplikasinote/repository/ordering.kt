package com.septian.nurfaozy.aplikasinote.repository

import com.septian.nurfaozy.aplikasinote.data.Note

sealed class OrderType{
    object Ascending : OrderType()
    object Descending : OrderType()
}

sealed class NoteOrder(val orderType:OrderType){
    class Title(orderType:OrderType):NoteOrder(orderType)
    class PlanAction(orderType:OrderType):NoteOrder(orderType)

    fun copy(orderType: OrderType):NoteOrder{
        return when(this){
            is PlanAction -> PlanAction(orderType)
            is Title -> Title(orderType)
        }
    }
}