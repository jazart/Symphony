package com.jazart.symphony.location;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.MainActivity.EXTRA_USER;
import static com.jazart.symphony.MainActivity.sDb;

public class LocationIntentService extends IntentService {
    public static final String TAG = "LocationIntentService";
    FusedLocationProviderClient mProviderClient;
    private LocationHelper mHelper;

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        final String userId = intent.getStringExtra(EXTRA_USER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String currentCity = getCityFromLocation(location);
                addToDb(location, currentCity, userId);

            }
        });
    }


    private void addToDb(Location location, String city, String userId) {
        DocumentReference reference = sDb.collection(USERS).document(userId);
        reference
                .update("location", new GeoPoint(location.getLatitude(), location.getLongitude()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LocationIntentService.this, "Cannot log location", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        reference.update("city", city)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //error;
                    }
                });
    }

    private String getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (addresses == null || addresses.size() < 1) {
            return null;
        }

        return addresses.get(0).getLocality();
    }

}
