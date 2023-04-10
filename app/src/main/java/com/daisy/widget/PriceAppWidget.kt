package com.daisy.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.daisy.R
import com.daisy.activity.mainActivity.MainActivity
import com.daisy.common.session.SessionManager
import com.daisy.pojo.response.Pricing
import com.daisy.utils.Constraint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class PriceAppWidget : AppWidgetProvider() {


fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.price_app_widget)
    val pricing: List<Pricing> = SessionManager.get().pricing
    var pricing1: Pricing? = null
    if (pricing != null && !pricing.isEmpty()) {
        OUTER_LOOP@ for (i in pricing.size - Constraint.ONE downTo Constraint.ZERO) {
            try {
                if (SessionManager.get().pricingPlainId
                        .equals(pricing[i].pricingPlanID)
                ) {
                    val sdf = SimpleDateFormat(Constraint.YYY_MM_DD)
                    var futureDate: Date? = if (pricing[i].getTimeExpires() != null) {
                        sdf.parse(pricing[i].getDateExpires() + " " + pricing[i].getTimeExpires())
                    } else {
                        sdf.parse(pricing[i].getDateExpires() + " " + Constraint.DEFAULT_HOURS_MINUTES)
                    }
                    var dateEffective: Date?
                    dateEffective = if (pricing[i].getTimeEffective() != null) {
                        sdf.parse(pricing[i].getDateEffective() + " " + pricing[i].getTimeEffective())
                    } else {
                        sdf.parse(pricing[i].getDateEffective() + " " + Constraint.DEFAULT_HOURS_MINUTES)
                    }
                    val todayDate = Date()
                    if (dateEffective != null && !dateEffective.after(todayDate)) {
                        if (futureDate != null && futureDate.after(todayDate)) {
                            pricing1 = pricing[i]
                            break@OUTER_LOOP
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (pricing1 == null) {
            for (i in Constraint.ZERO until pricing.size) {
                if (pricing[i].getIsDefault() != null && pricing[i].getIsDefault()
                        .equals(Constraint.ONE_STRING)
                ) {
                    pricing1 = pricing[i]
                }
            }
        }
    }
    if (pricing1 != null) {
        views.setTextViewText(R.id.first_val, pricing1.getPfv10())
        views.setTextViewText(R.id.second_val, pricing1.getPfv11())
        views.setTextViewText(R.id.ther_val, pricing1.getPfv12())
        views.setTextViewText(R.id.forth_val, pricing1.getPfv13())
        views.setTextViewText(R.id.fifth_val, pricing1.getPfv14())
        views.setTextViewText(R.id.sixth_val, pricing1.getPfv15())
    }

    views.setOnClickPendingIntent(
        R.id.open_mpc,
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    )

    // Instruct the widget manager to update the widget

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}


override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    val N = appWidgetIds.size

    // Perform this loop procedure for each App Widget that belongs to this provider
    for (i in 0 until N) {
        val appWidgetId = appWidgetIds[i]


        // Tell the AppWidgetManager to perform an update on the current app widget
        val views = RemoteViews(context.packageName, R.layout.price_app_widget)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

override fun onReceive(context: Context?, intent: Intent) {
    val action = intent.action
    if (action != null) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val name = ComponentName(context!!, PriceAppWidget::class.java)
        val appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(name)
        val N = appWidgetId.size
        if (N < 1) {
            return
        } else {
            val id = appWidgetId[N - 1]
           updateAppWidget(context, appWidgetManager, id)
        }
    } else {
        super.onReceive(context, intent)
    }
}

}