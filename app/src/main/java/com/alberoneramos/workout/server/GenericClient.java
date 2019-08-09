package com.alberoneramos.workout.server;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class GenericClient {
    Retrofit retrofit;

    GenericClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.myjson.com/bins/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
