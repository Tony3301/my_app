package com.example.moneyjet.room.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    //categorie of transaction
    val type: String,
    //description
    val desc: String,
    //it will work like as: +536r or -35r
    val pom: Float,
    //date, time
    val date: String
)