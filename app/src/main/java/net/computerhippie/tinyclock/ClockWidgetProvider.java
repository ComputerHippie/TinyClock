package net.computerhippie.tinyclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    DateFormat dateFormat = new SimpleDateFormat("d MMM");

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(AlarmClock.ACTION_SHOW_ALARMS), 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
            views.setOnClickPendingIntent(R.id.clock, pendingIntent);
            views.setTextViewText(R.id.time, timeFormat.format(new Date()));
            views.setTextViewText(R.id.date, dateFormat.format(new Date()));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(ACTION_AUTO_UPDATE)) {
            ComponentName clockWidget = new ComponentName(context.getPackageName(), ClockWidgetProvider.class.getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(clockWidget);
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
                views.setTextViewText(R.id.time, timeFormat.format(new Date()));
                views.setTextViewText(R.id.date, dateFormat.format(new Date()));
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // start alarm
        ClockWidgetAlarm appWidgetAlarm = new ClockWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        // TODO: alarm should be stopped only if all widgets has been disabled
        // stop alarm
        ClockWidgetAlarm appWidgetAlarm = new ClockWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }
}