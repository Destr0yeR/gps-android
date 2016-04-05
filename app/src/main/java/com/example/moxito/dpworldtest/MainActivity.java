package com.example.moxito.dpworldtest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    LocationManager locationNetworkManager;
    LocationManager locationGPSManager;
    LocationListener locationNetworkListener;
    LocationListener locationGPSListener;
    Location GPSLocation;
    Location NetworkLocation;
    Location currentBestLocation;
    GeocalizationService service;
    TextView latitud;
    TextView longitud;
    TextView status;
    Button start;
    Boolean isStopped = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitud = (TextView) findViewById(R.id.latitud);
        longitud = (TextView) findViewById(R.id.longitud);
        status = (TextView) findViewById(R.id.status);
        start = (Button) findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStopped){

                    start.setText("Stop");
                    isStopped = false;
                } else {
                    status.setText("Service is stopped");
                    start.setText("Start");
                    isStopped = true;
                }

            }
        });
        setupService();
        setupLocation();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://52.35.45.112:80/gps-ws/public/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        service = retrofit.create(GeocalizationService.class);
    }

    private void setupLocation() {
        locationGPSManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        locationNetworkManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);

        setupGPSListener();
        setupNetworkListener();
    }

    private void setupGPSListener(){
        locationGPSListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if ( !isStopped) {
                    GPSLocation = location;
                    if(GeoCalculate.isBetterLocation(GPSLocation,currentBestLocation))  {
                        Log.i("Location","GPS");
                        currentBestLocation = GPSLocation;
                    }

                    String latitudString =  Double.toString(currentBestLocation.getLatitude());
                    String longitudString = Double.toString(currentBestLocation.getLongitude());

                    latitud.setText("Latitud   : "+latitudString);
                    longitud.setText("Longitud : " + longitudString);
                    sendLocation(currentBestLocation);
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        requestLocationUpdates(locationGPSManager, locationGPSListener, 1);
    }

    private void setupNetworkListener(){

        locationNetworkListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if ( !isStopped) {
                    NetworkLocation = location;
                    if(GeoCalculate.isBetterLocation(NetworkLocation,currentBestLocation)) {
                        Log.i("Location","Network");
                        currentBestLocation = NetworkLocation;
                    }
                    String latitudString = Double.toString(currentBestLocation.getLatitude());
                    String longitudString = Double.toString(currentBestLocation.getLongitude());

                    latitud.setText("Latitud   : "+latitudString);
                    longitud.setText("Longitud : "+longitudString);
                    sendLocation(currentBestLocation);
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        requestLocationUpdates(locationNetworkManager, locationNetworkListener, 2);
    }

    private void sendLocation(Location location){
        String latitud = String.valueOf(location.getLatitude());
        String longitud = String.valueOf(location.getLongitude());
        Call<JsonElement> response = service.sendLocation("1",latitud,longitud);

        response.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if( response.isSuccessful()) {
                    JsonObject responseBody = response.body().getAsJsonObject();
                    if(responseBody.has("message")) {
                        status.setText("Status:"+responseBody.get("message").getAsString());
                    } else {
                        status.setText("Status:" + responseBody.toString());
                    }
                } else {
                    try {
                        status.setText("Status:"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("Retrofit",t.getMessage());
            }
        });
    }

    private void requestLocationUpdates(LocationManager locationManager, LocationListener locationListener, int type) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Coordenadas", "Coordenadas if");
            if (type == 1) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);
            }

            return;
        } else {
            Log.i("Coordendas", "Coordenadas else");
            if (type == 1) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locationListener);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.moxito.dpworldtest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.moxito.dpworldtest/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
