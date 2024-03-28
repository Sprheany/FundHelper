package com.sprheany.fundhelper.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "collection_funds",
    primaryKeys = ["code"],
    foreignKeys = [
        ForeignKey(entity = FundEntity::class, parentColumns = ["code"], childColumns = ["code"]),
    ]
)
@Serializable
data class CollectionFundEntity(
    val code: String,
    var sort: Float = 0f
)
