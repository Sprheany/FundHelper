package com.sprheany.fundhelper.glance

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.sprheany.fundhelper.models.FundState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object FundInfoStateDefinition : GlanceStateDefinition<FundState> {

    private const val DATA_STORE_FILENAME = "fundInfo"

    private val Context.dataStore by dataStore(
        fileName = DATA_STORE_FILENAME,
        serializer = FundSerializer
    )

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<FundState> {
        return context.dataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object FundSerializer : Serializer<FundState> {
        override val defaultValue: FundState
            get() = FundState.Error("no fund found")

        override suspend fun readFrom(input: InputStream): FundState = try {
            Json.decodeFromString(
                FundState.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read fund data: ${e.message}")
        }

        override suspend fun writeTo(t: FundState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(FundState.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}