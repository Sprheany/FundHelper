package com.sprheany.fundhelper.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sprheany.fundhelper.database.dao.CollectionFundDao
import com.sprheany.fundhelper.database.dao.FundDao
import com.sprheany.fundhelper.database.entities.CollectionFundEntity
import com.sprheany.fundhelper.database.entities.FundEntity

@Database(
    entities = [FundEntity::class, CollectionFundEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fundDao(): FundDao
    abstract fun collectionFundDao(): CollectionFundDao

    companion object {
        private const val DATABASE_NAME = "fund.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
