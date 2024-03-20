package com.sprheany.fundhelper.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.sprheany.fundhelper.database.entities.FundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {

    @Query("SELECT * FROM funds")
    fun getAll(): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds WHERE code = :code")
    suspend fun get(code: String): FundEntity?

    @Query(
        "SELECT * FROM funds " +
                "WHERE code LIKE '%'||:text||'%' " +
                "OR name LIKE '%'||:text||'%' " +
                "OR py LIKE '%'||:text||'%' "
    )
    fun find(text: String): Flow<List<FundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg fund: FundEntity)

    @Upsert
    suspend fun upsert(vararg fund: FundEntity)

    @Delete
    suspend fun delete(fund: FundEntity)
}