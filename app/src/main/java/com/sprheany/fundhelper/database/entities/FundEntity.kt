package com.sprheany.fundhelper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "funds")
@Serializable
data class FundEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val pinyin: String = "",
    val py: String = "",
    val type: String = "",
    var isCollection: Boolean = false,
)
