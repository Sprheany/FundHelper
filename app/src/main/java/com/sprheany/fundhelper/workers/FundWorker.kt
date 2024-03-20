package com.sprheany.fundhelper.workers

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sprheany.fundhelper.glance.FundAppWidget
import com.sprheany.fundhelper.glance.FundInfoStateDefinition
import com.sprheany.fundhelper.models.FundState
import com.sprheany.fundhelper.usecase.FundUseCase
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class FundWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private val uniqueWorkName = FundWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<FundWorker>(
                15.minutes.toJavaDuration(),
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            }
            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build(),
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(FundAppWidget::class.java)

        return try {
            setWidgetState(glanceIds, FundState.Loading)
            FundUseCase.collectionFundWorthFlow.collect {
                val state = FundState.Success(it)
                setWidgetState(glanceIds, newState = state)
            }
            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, FundState.Error(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: FundState) {
        glanceIds.forEach {
            updateAppWidgetState(context = context,
                glanceId = it,
                definition = FundInfoStateDefinition,
                updateState = { newState })
        }
        FundAppWidget().updateAll(context)
    }
}