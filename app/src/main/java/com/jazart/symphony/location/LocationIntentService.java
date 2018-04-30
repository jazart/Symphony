package com.jazart.symphony.location;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.MainActivity.EXTRA_USER;
import static com.jazart.symphony.MainActivity.sDb;

public class LocationIntentService extends IntentService {
    FusedLocationProviderClient mProviderClient;

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
                addToDb(location, userId);
            }
        });
    }


    private void addToDb(Location location, String userId) {
        sDb.collection(USERS).document(userId)
                .update("location", new GeoPoint(location.getLatitude(), location.getLatitude()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LocationIntentService.this, "Cannot log location", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

}
