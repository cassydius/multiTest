package com.beyowi.multitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Button buttonScan;
    Button buttonGPS;
    Button buttonWebView;
    Button buttonCustomVisual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScan = (Button) findViewById(R.id.button_scan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getBaseContext(), com.beyowi.multitest.ScanActivity.class);
                startActivity(scanIntent);
            }
        });
        buttonGPS = (Button) findViewById(R.id.button_gps);
        buttonGPS.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gpsIntent = new Intent(getBaseContext(), com.beyowi.multitest.GPSActivity.class);
                startActivity(gpsIntent);
            }
        });
        buttonWebView = (Button) findViewById(R.id.button_webview);
        buttonWebView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wvIntent = new Intent(getBaseContext(), WebViewActivity.class);
                startActivity(wvIntent);
            }
        });

        buttonCustomVisual = (Button) findViewById(R.id.button_custom_visual);
        buttonCustomVisual.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cvIntent = new Intent(getBaseContext(), CustomVisualActivity.class);
                startActivity(cvIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
