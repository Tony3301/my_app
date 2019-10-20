package com.example.moneyjet.room.categs

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CategInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    //category name
    var name: String,
    //color id
    var colorId: Int,
    //exp or income?(0 - expenses ; 1 - income)
    var income: Int
)