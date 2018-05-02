package com.jazart.symphony.venues;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface FoursquareApiClient {


//    @Headers({
//            "client_id: " + client_id,
//            "client_secret: " + CLIENT_SECRET
//
//    })

    @GET("search")
    Call<VenueResponse> getVenues(
            @QueryMap Map<String, String> options
    );

    @GET("{venue_id}/pics")
    Call<VenuPicsResponse> getVenuepics(@Path("venue_id") int venueId);
}
