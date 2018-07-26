package com.jazart.symphony.network;

import com.jazart.symphony.model.venues.VenuePicsResponse;
import com.jazart.symphony.model.venues.VenueResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface FoursquareApiClient {

    @GET("search")
    Call<VenueResponse> getVenues(
            @QueryMap Map<String, String> options
    );

    @GET("{venue_id}/photos")
    Call<VenuePicsResponse> getVenuePics(@Path("venue_id") String venueId,
                                         @QueryMap Map<String, String> options
                                         );
}
