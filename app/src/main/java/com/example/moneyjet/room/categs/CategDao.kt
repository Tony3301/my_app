package com.example.moneyjet.room.categs

import androidx.room.*


@Dao
interface CategDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCateg(Categ: CategInfo)

    @Update
    fun updateCateg(Categ: CategInfo)

    @Delete
    fun deleteCateg(Categ: CategInfo)

    @Query("DELETE FROM CategInfo WHERE name = :name")
    fun deleteCategByName(name: String)

    @Query("SELECT * FROM CategInfo")
    fun getCategs(): List<CategInfo>

    @Query("SELECT * FROM CategInfo WHERE income = 1")
    fun getCategsIncome(): List<CategInfo>

    @Query("SELECT * FROM CategInfo WHERE income = 0")
    fun getCategsExpense(): List<CategInfo>

    @Query("SELECT * FROM CategInfo WHERE income = :name")
    fun getCategByName(name: String): CategInfo

    @Query("SELECT * FROM CategInfo WHERE colorId = :color")
    fun getCategByColorId(color: String): List<CategInfo>

    @Query("SELECT * FROM CategInfo WHERE income = :type")
    fun getCategByType(type: Int): CategInfo

    @Query("SELECT CASE WHEN EXISTS (SELECT * FROM CategInfo WHERE name = :name ) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END")
    fun catExist(name: String): Int

    //get the last id
    @Query("SELECT MAX(id) AS MaxID FROM CategInfo")
    fun getLastID(): Int
}
