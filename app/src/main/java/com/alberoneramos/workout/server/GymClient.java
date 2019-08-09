package com.alberoneramos.workout.server;

import com.alberoneramos.workout.models.Locations;

import retrofit2.Call;
import retrofit2.http.GET;

public class GymClient extends GenericClient {

    public LocationService getApiService() {
        return retrofit.create(LocationService.class);
    }

    public interface LocationService {
        @GET("sunns")
        Call<Locations> getLocations();
    }
}