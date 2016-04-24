package net.computerhippie.tinyclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

public class ClockWidgetConfigure extends Activity {
    private static final int minSize = 12;
    private static final int sizeStep = 5;
    private static final int defaultSize = 18;

    private int appWidgetId;

    private int hours = 24;
    private int lines = 2;
    private int size = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // set color picker
        ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        picker.setShowOldCenterColor(false);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        picker.addSVBar(svBar);
        svBar.setSaturation(0);

        SeekBar sizeBar = (SeekBar) findViewById(R.id.size);
        sizeBar.setProgress(sizeStep * (defaultSize - minSize));
        ((TextView)findViewById(R.id.sizevalue)).setText(defaultSize + "");
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = minSize + (int)Math.ceil(progress / sizeStep);
                ((TextView)findViewById(R.id.sizevalue)).setText(size + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void OkClick(View v){
        setWidget();
    }

    public void PreferencesClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.h12: hours = 12; break;
            case R.id.h24: hours = 24; break;
            case R.id.oneline: lines = 1; break;
            case R.id.twolines: lines = 2; break;
        }
    }

    public void setWidget() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putInt("hours", hours);
        editor.putInt("lines", lines);
        editor.putInt("size", size);
        editor.putInt("color", ((SVBar) findViewById(R.id.svbar)).getColor());
        editor.commit();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, ClockWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        sendBroadcast(intent);

        finish();
    }
}