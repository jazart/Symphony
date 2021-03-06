package com.jazart.symphony.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.jazart.symphony.common.Constants;
import com.jazart.symphony.common.MainActivity;
import com.jazart.symphony.R;
import com.jazart.symphony.di.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import javax.inject.Inject;

import static com.jazart.symphony.common.Constants.USERS;

/**
 * Service that runs when the app first starts up. This service gets the user's current location
 * and stores the exact coordinates and city name in the database.
 **/

@SuppressLint("MissingPermission")
public class LocationIntentService extends JobIntentService {

    public final String TAG = "LocationIntentService";
    private String userId;

    @Inject
    FirebaseFirestore db;

    public LocationIntentService() {
        super();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LocationIntentService.class, 1, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).component.inject(this);
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.channel_name))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.acq_loc))
                .setPriority(3)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(TAG, 1, notification);
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        userId = intent.getStringExtra(MainActivity.getEXTRA_USER());
        Location locationResult = intent.getParcelableExtra("loc");
        String currentCity = getCityFromLocation(locationResult);
        if(userId != null) {
            addToDb(locationResult, currentCity, userId);
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
    }

    private void addToDb(Location location, String city, String userId) {
        DocumentReference reference = db.collection(USERS).document(userId);
        reference
                .update(Constants.LOCATION, new GeoPoint(location.getLatitude(), location.getLongitude()))
                .addOnFailureListener(e -> Toast.makeText(LocationIntentService.this, "Cannot log location", Toast.LENGTH_SHORT)
                        .show());
        reference.update(Constants.CITY, city);
    }

    private String getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        return getAddress(geocoder, location)
                .stream()
                .findFirst()
                .map(Address::getLocality)
                .orElse("Not Found");
    }

    private List<Address> getAddress(Geocoder geocoder, Location location) {
        try {
            return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
