package com.example.moneyjet.room.transactions

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemInfo::class], version = 1, exportSchema = false)

abstract class TransactionsDatabase : RoomDatabase() {
    abstract fun ItemDao(): ItemDao

    companion object {
        var INSTANCE: TransactionsDatabase? = null

        fun getAppDataBase(context: Context): TransactionsDatabase? {
            if (INSTANCE == null){
                synchronized(TransactionsDatabase::class){
                    INSTANCE = Room.databaseBuilder(context,
                        TransactionsDatabase::class.java, "transactionsDB").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}