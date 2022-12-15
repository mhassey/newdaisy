package com.iris.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.SizeF;
import android.widget.RemoteViews;

import com.iris.R;
import com.iris.activity.mainActivity.MainActivity;
import com.iris.activity.splash.SplashScreen;
import com.iris.common.session.SessionManager;
import com.iris.pojo.response.Pricing;
import com.iris.utils.Constraint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class PriceCardWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_card_widget);
        List<Pricing> pricing = SessionManager.get().getPricing();
        Pricing pricing1 = null;
        if (pricing!=null && !pricing.isEmpty())
        {
            OUTER_LOOP:
            for (int i = (pricing.size() - Constraint.ONE); i >= Constraint.ZERO; i--) {
                try {
                    if (SessionManager.get().getPricingPlainId().equals(pricing.get(i).getPricingPlanID())) {
                        SimpleDateFormat sdf = new SimpleDateFormat(Constraint.YYY_MM_DD);
                        Date futureDate;
                        if (pricing.get(i).getTimeExpires() != null) {
                            futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + pricing.get(i).getTimeExpires());

                        } else {
                            futureDate = sdf.parse(pricing.get(i).getDateExpires() + " " + Constraint.DEFAULT_HOURS_MINUTES);

                        }
                        Date dateEffective;
                        if (pricing.get(i).getTimeEffective() != null) {
                            dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + pricing.get(i).getTimeEffective());

                        } else {
                            dateEffective = sdf.parse(pricing.get(i).getDateEffective() + " " + Constraint.DEFAULT_HOURS_MINUTES);

                        }
                        Date todayDate = new Date();

                        if (dateEffective != null && !dateEffective.after(todayDate)) {
                            if (futureDate != null && futureDate.after(todayDate)) {
                                pricing1 = pricing.get(i);
                                break OUTER_LOOP;
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (pricing1 == null) {
                for (int i = Constraint.ZERO; i < pricing.size(); i++) {
                    if (pricing.get(i).getIsDefault() != null && pricing.get(i).getIsDefault().equals(Constraint.ONE_STRING)) {
                        pricing1 = pricing.get(i);
                    }

                }
            }

        }
        if (pricing1!=null)
        {

            views.setTextViewText(R.id.first_val, pricing1.getPfv10());
            views.setTextViewText(R.id.second_val, pricing1.getPfv11());
            views.setTextViewText(R.id.ther_val, pricing1.getPfv12());
            views.setTextViewText(R.id.forth_val, pricing1.getPfv13());
            views.setTextViewText(R.id.fifth_val, pricing1.getPfv14());
            views.setTextViewText(R.id.sixth_val, pricing1.getPfv15());

        }

        views.setOnClickPendingIntent(R.id.open_mpc,
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];



            // Tell the AppWidgetManager to perform an update on the current app widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_card_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);




        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action != null) {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName name = new ComponentName(context, PriceCardWidget.class);
            int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
            final int N = appWidgetId.length;
            if (N < 1)
            {
                return ;
            }
            else {
                int id = appWidgetId[N-1];
                updateAppWidget(context, appWidgetManager, id);
            }
        }

        else {
            super.onReceive(context, intent);
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