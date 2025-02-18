package com.paligot.confily.core.sample

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paligot.confily.core.events.EventRepository
import org.koin.core.component.KoinComponent

class ScheduleWorkManager(
    context: Context,
    parameters: WorkerParameters,
    private val repository: EventRepository
) : CoroutineWorker(context, parameters), KoinComponent {
    override suspend fun doWork(): Result {
        return try {
            repository.fetchAndStoreAgenda()
            Result.success()
        } catch (_: Throwable) {
            Result.failure()
        }
    }
}
