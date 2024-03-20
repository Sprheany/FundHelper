package com.sprheany.fundhelper.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.sprheany.fundhelper.workers.FundWorker

class FundAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FundAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        FundWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        FundWorker.cancel(context)
    }
}