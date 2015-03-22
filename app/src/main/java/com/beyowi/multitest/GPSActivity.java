package com.beyowi.multitest;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class GPSActivity extends ActionBarActivity {

    TextView latitude;
    TextView longitude;
    TextView closestRestaurant;
    Button getDataCoord;
    Button getGPSCoord;
    Button pickPosition;
    Button pickAddress;
    Map map;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        closestRestaurant = (TextView) findViewById(R.id.closest_restaurant);
        getDataCoord = (Button) findViewById(R.id.data_local_button);
        getDataCoord.setOnClickListener(new onGetDataClick());
        getGPSCoord = (Button) findViewById(R.id.gps_local_button);
        getGPSCoord.setOnClickListener(new onGetGPSClick());
        pickAddress = (Button) findViewById(R.id.address_button);
        pickPosition = (Button) findViewById(R.id.position_button);


        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                updateLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {
                // Provider not enabled, prompt user to enable it
                Toast.makeText(getBaseContext(), R.string.please_turn_on_gps, Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        };

    }


    public class onGetDataClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public class onGetGPSClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_g, menu);
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

    public void updateLocation(Location location){

        Log.i("MULTITESTLOG", "Lat:" + location.getLatitude() + " Long:" + location.getLongitude());
        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));

        // Remove the listener you previously added
        locationManager.removeUpdates(locationListener);
    }
}
