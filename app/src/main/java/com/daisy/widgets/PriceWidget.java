package com.daisy.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;

import com.daisy.R;
import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.splash.SplashScreen;

/**
 * Implementation of App Widget functionality.
 */
public class PriceWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_widget);
//        views.setTextViewText(R.id.open_mpc, widgetText);
        Intent intent=new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_MUTABLE);
        views.setOnClickPendingIntent(R.id.open_mpc,pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


}