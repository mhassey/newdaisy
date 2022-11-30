package com.iris.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.SizeF;
import android.widget.RemoteViews;

import com.iris.R;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class PriceCardWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_card_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        HashMap viewMapping = new HashMap();
        viewMapping.put(new SizeF(100f, 50f), new RemoteViews(context.getPackageName(), R.layout.price_card_widget));
        viewMapping.put(new SizeF(150f, 50f), new RemoteViews(context.getPackageName(), R.layout.price_card_widget_large));
        viewMapping.put(new SizeF(215f, 50f), new RemoteViews(context.getPackageName(), R.layout.price_card_widget_medium));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            RemoteViews remoteViews = new RemoteViews(viewMapping);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}