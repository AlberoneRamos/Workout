package com.alberoneramos.workout.server;

import com.alberoneramos.workout.models.Locations;

import retrofit2.Call;
import retrofit2.http.GET;

public class PracaClient extends GenericClient {

    public LocationService getApiService() {
        return retrofit.create(LocationService.class);
    }

    public interface LocationService {
        @GET("8w0x5")
        Call<Locations> getLocations();
    }
}