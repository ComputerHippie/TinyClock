package net.computerhippie.tinyclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "net.computerhippie.tinyclock.AUTO_UPDATE";

    DateFormat time24Format = new SimpleDateFormat("HH:mm");
    DateFormat time12Format = new SimpleDateFormat("h:mm a");
    DateFormat dateFormat = new SimpleDateFormat("d MMM");

    private int hours;
    private int lines;
    private int size;
    private int color;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if(action.equals(ACTION_AUTO_UPDATE) || action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) || action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
            restorePreferences(context);
            ComponentName clockWidget = new ComponentName(context.getPackageName(), ClockWidgetProvider.class.getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(clockWidget);
            updateWidgetView(context, appWidgetManager, appWidgetIds);

            ClockWidgetAlarm.startIfNeeded(context);
        }
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        ComponentName clockWidget = new ComponentName(context.getPackageName(), ClockWidgetProvider.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] existsAppWidgetIds = appWidgetManager.getAppWidgetIds(clockWidget);
        if(existsAppWidgetIds.length == 0)
            ClockWidgetAlarm.stopAlarm(context);

        super.onDeleted(context, appWidgetIds);
    }

    private void updateWidgetView(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(AlarmClock.ACTION_SHOW_ALARMS), 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
            views.setOnClickPendingIntent(R.id.clock, pendingIntent);

            views.setTextViewText(R.id.time, getTimeString());
            views.setTextColor(R.id.time, color);
            views.setTextViewTextSize(R.id.time, TypedValue.COMPLEX_UNIT_DIP, size);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private String getTimeString() {
        DateFormat timeFormat = hours == 24 ? time24Format : time12Format;
        return timeFormat.format(new Date()).toLowerCase() +
                (lines == 2 ? "\n" : " ") +
                dateFormat.format(new Date());
    }

    private void restorePreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TinyClockPreferences", Context.MODE_PRIVATE);
        hours = preferences.getInt("hours", 24);
        lines = preferences.getInt("lines", 2);
        size = preferences.getInt("size", 18);
        color = preferences.getInt("color", -1);
    }
}