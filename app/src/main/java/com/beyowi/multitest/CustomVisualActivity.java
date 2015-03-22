package com.beyowi.multitest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;


public class CustomVisualActivity extends ActionBarActivity {

    Button buttonStart;
    Button buttonStop;
    Chronometer chrono;
    Long lastPause;
    String FONTPATH = "fonts/digital-7.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_visual);

        //Load font
        Typeface chronoFont = Typeface.createFromAsset(getAssets(), FONTPATH);
        chrono = (Chronometer) findViewById(R.id.chrono);
        chrono.setTypeface(chronoFont);

        lastPause = SystemClock.elapsedRealtime();

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chrono.setBase(chrono.getBase() + SystemClock.elapsedRealtime() - lastPause);
                chrono.start();
            }
        });

        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPause = SystemClock.elapsedRealtime();
                chrono.stop();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_visual, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
