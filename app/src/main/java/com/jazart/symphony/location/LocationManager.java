package com.jazart.symphony.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jazart.symphony.model.User;

import java.util.ArrayList;
import java.util.List;

public class LocationManager {
    private FusedLocationProviderClient mLocationProviderClient;
    private Location mCurrentLocation;
    private Context mContext;
    private Geofence mGeofence;

    public LocationManager(Context context) {
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }


    public boolean pingLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        mLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mCurrentLocation = location;
                        buildGeoFence();

                    }
                });
        return true;

    }

    public List<User> getNearByUsers(User user) {
        return new ArrayList<User>();
    }


    private void buildGeoFence() {
        mGeofence = new Geofence.Builder()
                .setCircularRegion(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        10000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder()
                .addGeofence(mGeofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        GeofencingClient client = new GeofencingClient(mContext);


    }
}
