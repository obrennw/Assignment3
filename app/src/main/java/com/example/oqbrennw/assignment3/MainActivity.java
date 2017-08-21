package com.example.oqbrennw.assignment3;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, OnMapReadyCallback, ResultCallback{



    GoogleApiClient client = null;
    private LocationRequest req = null;
    private GoogleMap gmap = null;
    private Location curloc = null;
    private AddressResultReceiver mResultReceiver;
    private ArrayList<Geofence> fence_list;
    private PendingIntent mGeofencePendingIntent;
    public MediaPlayer mp1,mp2,mp3,mp4;
    public boolean prepared;
    public int val = 0;
    String mAddressOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (client == null) {
            client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        fence_list = new ArrayList<Geofence>();
        fence_list.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("polk_place")

                .setCircularRegion(
                        Constants.polk_place_lat,
                        Constants.polk_place_lon,
                        40
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(1000000)
                .build());
        fence_list.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("brooks")
                .setCircularRegion(
                        Constants.brooks_lat,
                        Constants.brooks_lon,
                        56
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(1000000)
                .build());
        fence_list.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("old_well")

                .setCircularRegion(
                        Constants.old_well_lat,
                        Constants.old_well_lon,
                        40
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(1000000)
                .build());
        fence_list.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("home")

                .setCircularRegion(
                        Constants.home_lat,
                        Constants.home_lon,
                        20
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(1000000)
                .build());
        fence_list.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("davie")

                .setCircularRegion(
                        Constants.davie_lat,
                        Constants.davie_lon,
                        20
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(1000000)
                .build());


        Log.v("TAG","start");

        mp1 = MediaPlayer.create(getApplicationContext(),R.raw.cake_by_the_ocean);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.waves);
        mp3 = MediaPlayer.create(getApplicationContext(),R.raw.carolina_in_my_mind);

        prepared = true;


        mediaPlayerController();



    }


    public void mediaPlayerController(){
        if(prepared) {
            if (val == 1 && !mp1.isPlaying()) {
                mp1 = MediaPlayer.create(getApplicationContext(),R.raw.cake_by_the_ocean);
                mp1.start();
            }
            else if (val == 2 && !mp2.isPlaying()) {
                mp2 = MediaPlayer.create(getApplicationContext(),R.raw.waves);
                mp2.start();
            }
            else if (val == 3 && !mp3.isPlaying()){
                mp3 = MediaPlayer.create(getApplicationContext(),R.raw.carolina_in_my_mind);
                mp3.start();
            }
            else if (val == 0){
                if (mp1.isPlaying()){
                    mp1.stop();
                }
                else if (mp2.isPlaying()){
                    mp2.stop();
                }
                else if (mp3.isPlaying()){
                    mp3.stop();
                }
            }

        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(fence_list);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(android.os.Message message) {
                String result;
                double lat = 0, lon = 0;
                int val2 = 0;
                switch (message.what) {
                    case 1:
                        Bundle bundle = message.getData();
                        result = bundle.getString("place");
                        lat = bundle.getDouble("lat");
                        lon = bundle.getDouble("lon");
                        val2 = bundle.getInt("val");

                        gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(result));
                        val = val2;
                        break;
                    case 2:
                        gmap.clear();
                        val = 0;
                        if (mp1.isPlaying()){
                            mp1.stop();
                        }
                        else if (mp2.isPlaying()){
                            mp2.stop();
                        }
                        else if (mp3.isPlaying()){
                            mp3.stop();
                        }
                    default:
                        result = null;
                }
                mediaPlayerController();

            }

        };

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra("messenger",new Messenger(handler));

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        req = new LocationRequest();
        req.setInterval(10000);
        req.setFastestInterval(1000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
                TextView tv = (TextView)findViewById(R.id.textView1);
                curloc = LocationServices.FusedLocationApi.getLastLocation(client);
                tv.setText("Longitude: " + curloc.getLongitude()+ " Latitude: " + curloc.getLatitude());
                Log.v("LOC", "" + curloc.getLatitude() + ", " + curloc.getLongitude());
                //getAddressFromLocation(curloc,this,new GeocoderHandler());
                resumeLocationUpdates();

        }catch (SecurityException ex) {
            ex.printStackTrace();    }
        catch (Exception e){
            Log.v("ERROR","Something went wrong");
        }

        LocationServices.GeofencingApi.addGeofences(
                client,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

        mediaPlayerController();

    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, curloc);
        startService(intent);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        client.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        client.disconnect();
        if (mp1.isPlaying()){
            mp1.stop();
        }
        else if (mp2.isPlaying()){
            mp2.stop();
        }
        else if (mp3.isPlaying()){
            mp3.stop();
        }
        super.onStop();

    }

    /*public static void addPoint(double lat, double lon, String title){
        gmap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(title));
        switch (title){
            case "brooks":
                break;
            case "polk_place":
                break;
            case "old_well":
                break;
            case "home":
                break;
            case "davie":
                break;
        }
        gmap.clear();
    }

    public static void clearMap(String title){
        gmap.clear();
    }*/

    public static void getAddressFromLocation(final Location location, final Context context, final GeocoderHandler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        android.location.Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality();
                    }
                } catch (IOException e) {
                    Log.e("TAG", "Impossible to connect to Geocoder", e);
                } finally {
                    android.os.Message msg = android.os.Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        bundle.putDouble("lat", location.getLatitude());
                        bundle.putDouble("lon",location.getLongitude());
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (client.isConnected()) {
            resumeLocationUpdates();
        }
        mediaPlayerController();

    }
    private void resumeLocationUpdates() {
        Log.v("RESUMING", "RESUMING LOCATION UPDATES");
        req = new LocationRequest();
        req.setInterval(10000);
        req.setFastestInterval(1000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, req, this);
    }

    private void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(client.isConnected()) {
            stopLocationUpdates();
        }
        if (mp1.isPlaying()){
            mp1.stop();
        }
        else if (mp2.isPlaying()){
            mp2.stop();
        }
        else if (mp3.isPlaying()){
            mp3.stop();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        /*gmap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(),location.getLongitude()))
                .title("marker"));*/
        getAddressFromLocation(location,this,new GeocoderHandler());
        mediaPlayerController();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        gmap = map;
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.brooks_lat,Constants.brooks_lon),17));

        /*gmap.addMarker(new MarkerOptions()
                .position(new LatLng(Constants.brooks_lat,Constants.brooks_lon))
                .title("Marker"));*/
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        //displayAddressOutput();

        Toast.makeText(this, mAddressOutput,
                Toast.LENGTH_LONG).show();

        // Show a toast message if an address was found.
        /*if (resultCode == Constants.SUCCESS_RESULT) {
            Toast.makeText(this, "Address Found",
                    Toast.LENGTH_LONG).show();
        }*/

    }

    @Override
    public void onResult(@NonNull Result result) {

        result.getStatus();
    }


    private class GeocoderHandler extends android.os.Handler {
        @Override
        public void handleMessage(android.os.Message message) {
            String result;
            double lat = 0,lon=0;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    lat = bundle.getDouble("lat");
                    lon = bundle.getDouble("lon");
                    break;
                default:
                    result = null;
            }
            // replace by what you need to do
           TextView tv1 = (TextView) findViewById(R.id.textView1);
            tv1.setText(result);
            /*gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(result));*/
        }

    }




}

class AddressResultReceiver extends ResultReceiver {
    public AddressResultReceiver(android.os.Handler handler) {
        super(handler);
    }
}