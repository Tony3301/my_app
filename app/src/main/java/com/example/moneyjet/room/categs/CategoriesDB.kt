package com.example.moneyjet.room.categs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CategInfo::class], version = 1, exportSchema = false)

abstract class CategoriesDatabase : RoomDatabase() {
    abstract fun CategDao(): CategDao

    companion object {
        var INSTANCE: CategoriesDatabase? = null

        fun getAppDataBase(context: Context): CategoriesDatabase? {
            if (INSTANCE == null){
                synchronized(CategoriesDatabase::class){
                    INSTANCE = Room.databaseBuilder(context,
                        CategoriesDatabase::class.java, "categsDB").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}