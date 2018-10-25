package com.jazart.symphony.venues;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.net.Uri;
import android.text.TextUtils;

import com.jazart.symphony.model.venues.Item;
import com.jazart.symphony.model.venues.Venue;
import com.jazart.symphony.network.FoursquareApiClient;
import com.jazart.symphony.network.FoursquareConstants;
import com.jazart.symphony.network.NetworkService;
import com.jazart.symphony.repository.LocationHelperRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Viewmodel class used for loading information from the foursquare api and
 * wrappinng the data in livedata objects for a more reactive interface.
 */
class VenueViewModel extends ViewModel {

    private static final String TAG = "VenueViewModel";


    private MutableLiveData<List<Venue>> mVenueLiveData;

    LiveData<List<Venue>> getVenues() {
        loadVenues();
        if(mVenueLiveData == null) {
            mVenueLiveData = new MutableLiveData<>();
        }

        return mVenueLiveData;
    }

    private void loadVenues() {
        Retrofit client = NetworkService.get().getService();

        FoursquareApiClient apiClient = client.create(FoursquareApiClient.class);
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") Map<String, String> options = new HashMap<>();
        options.put("client_id", FoursquareConstants.client_id);
        options.put("client_secret", FoursquareConstants.CLIENT_SECRET);
        options.put("ll", LocationHelperRepo.getInstance().getUserLocation());
        options.put("intent", "checkin");
        options.put("radius", "10000");
        options.put("limit", "10");
        options.put("categoryId", "4bf58dd8d48988d1ac941735,4bf58dd8d48988d1e5931735");
        options.put("v", "20180502");
    }

    private Uri buildUri(Item photo) {
        CharSequence uri = TextUtils.concat(photo.getPrefix(), ""
                + photo.getWidth()
                +"" +
                photo.getHeight(), photo.getSuffix());
        return Uri.parse(uri.toString());
    }

    private Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("client_id", FoursquareConstants.client_id);
        options.put("client_secret", FoursquareConstants.CLIENT_SECRET);
        options.put("limit", "1");
        options.put("v", "20180502");
        return options;
    }
}
