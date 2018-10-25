package com.jazart.symphony.network

import androidx.lifecycle.MutableLiveData
import com.jazart.symphony.model.venues.Venue
import com.jazart.symphony.model.venues.VenuePicsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class FoursquareRepo @Inject constructor(val service: Retrofit?) {

    private val client by lazy {
        service?.create(FoursquareApiClient::class.java)
    }

    private var options: MutableMap<String, String>? = null
    private val venuePicsLiveData: MutableLiveData<Venue>? = null
    private val venueLiveData: MutableLiveData<Venue>? = null


    fun getPictures(): MutableLiveData<Venue>? {
        return venueLiveData
    }

    fun getVenue(): MutableLiveData<Venue>? {
        val call = client?.getVenuePics("", options)
        call?.enqueue(object : Callback<VenuePicsResponse> {
            override fun onResponse(call: Call<VenuePicsResponse>?, response: Response<VenuePicsResponse>?) {

            }

            override fun onFailure(call: Call<VenuePicsResponse>, t: Throwable) {

            }

        })

        return null
    }
}