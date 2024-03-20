package com.sprheany.fundhelper.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sprheany.fundhelper.usecase.FundUseCase
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class UpdateAllFundWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private val uniqueWorkName = UpdateAllFundWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)

            val requestBuilder = PeriodicWorkRequestBuilder<UpdateAllFundWorker>(
                24.hours.toJavaDuration(),
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()
            )

            var workPolicy = ExistingPeriodicWorkPolicy.KEEP
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result = try {
        FundUseCase.requestAllFund()
        Result.success()
    } catch (e: Exception) {
        Result.failure()
    }
}