package com.example.moneyjet.room.transactions

import androidx.room.*

@Dao
interface ItemDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(Item: ItemInfo)

    @Update
    fun updateItem(Item: ItemInfo)

    @Delete
    fun deleteGender(Item: ItemInfo)

    @Query("SELECT * FROM ItemInfo")
    fun getItems(): List<ItemInfo>

    @Query("SELECT * FROM ItemInfo WHERE type = :name")
    fun getItemsByName(name: String): List<ItemInfo>

    @Query("SELECT * FROM ItemInfo WHERE type = :name AND date = :date")
    fun getTodaysItemsByName(name: String, date: String): List<ItemInfo>

    @Query("SELECT * FROM ItemInfo WHERE date = :date")
    fun getItemsByDate(date: String): List<ItemInfo>

    @Query("SELECT MAX(id) AS MaxID FROM ItemInfo")
    fun getMaxID(): Int
}
