package com.sprheany.fundhelper.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.sprheany.fundhelper.database.entities.CollectionFundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionFundDao {

    @Query("SELECT * FROM collection_funds order by sort")
    fun getAll(): Flow<List<CollectionFundEntity>>

    @Query("SELECT max(sort) FROM collection_funds")
    suspend fun getMaxSort(): Float

    @Query("SELECT * FROM collection_funds WHERE code = :code")
    suspend fun get(code: String): CollectionFundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg fund: CollectionFundEntity)

    @Upsert
    suspend fun upsert(vararg fund: CollectionFundEntity)

    @Delete
    suspend fun delete(fund: CollectionFundEntity)
}