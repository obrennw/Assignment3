package com.example.oqbrennw.assignment3;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.nearby.messages.Message;

import java.util.List;

/**
 * Created by oqbrennw on 3/25/17.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    String place;
    public GeofenceTransitionsIntentService(){
        super("");
    }
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = geofencingEvent.getErrorCode() + "";
            Log.e("TAG", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        // Test that the reported transition was of interest.
        for (Geofence g: triggeringGeofences){
            place = g.getRequestId();
        }

        Log.v("TAG", "doing stuff here");

        double lat=0,lon=0;
        int val = 0;
        switch (place){
            case "brooks":
                lat = Constants.brooks_lat;
                lon = Constants.brooks_lon;
                val = 1;
                break;
            case "polk_place":
                lat = Constants.polk_place_lat;
                lon = Constants.polk_place_lon;
                val = 2;
                break;
            case "old_well":
                lat = Constants.old_well_lat;
                lon = Constants.old_well_lon;
                val = 3;
                break;
            case "home":
                lat = Constants.home_lat;
                lon = Constants.home_lon;
                val = 1;
                break;
            case "davie":
                lat = Constants.davie_lat;
                lon = Constants.davie_lon;
                val = 2;
                break;
            default:
                lat =0;
                lon =0;
                val =0;
                break;
        }



        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Messenger messenger = (Messenger) bundle.get("messenger");
            android.os.Message msg = android.os.Message.obtain();
            bundle.putString("place", place);
            bundle.putDouble("lat", lat);
            bundle.putDouble("lon",lon);

             //put the data here
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
                bundle.putInt("val", val);
                msg.what = 1;
            }
            else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
                bundle.putInt("val", val);
                msg.what = 2;
            }
            else {
                // Log the error.
                Log.e("TAG", "geofence_transition_invalid_type" +
                        geofenceTransition);
            }
            msg.setData(bundle);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }

        }
    }

}
