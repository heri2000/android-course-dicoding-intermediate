package com.rinjaninet.mywidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class RandomNumbersWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.random_numbers_widget)
        val lastUpdate = "Random: " + NumberGenerator.generate(100)
        views.setTextViewText(R.id.appwidget_text, lastUpdate)
        views.setOnClickPendingIntent(R.id.btn_click, getPendingSelfIntent(context, appWidgetId, WIDGET_CLICK))
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (WIDGET_CLICK == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.random_numbers_widget)
            val lastUpdate = "Random: " + NumberGenerator.generate(100)
            val appWidgetId = intent.getIntExtra(WIDGET_ID_EXTRA, 0)
            views.setTextViewText(R.id.appwidget_text, lastUpdate)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getPendingSelfIntent(context: Context, appWidgetId: Int, action: String): PendingIntent {
        val intent = Intent(context, RandomNumbersWidget::class.java)
        intent.action = action
        intent.putExtra(WIDGET_ID_EXTRA, appWidgetId)
        return  PendingIntent.getBroadcast(context, appWidgetId, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
        )
    }

    companion object {
        private const val WIDGET_CLICK = "android.appwidget.action.APPWIDGET_UPDATE"
        private const val WIDGET_ID_EXTRA = "widget_id_extra"
    }
}
