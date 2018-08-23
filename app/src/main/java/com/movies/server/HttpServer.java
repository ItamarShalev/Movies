package com.movies.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.movies.data.MovieData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * this class give interface from Retrofit for to do http request just call to HttpServer#getInstanceRequest
 */
public class HttpServer {

    private static final String BASE_URL = "https://api.androidhive.info/";
    private static HttpRequest httpRequest;

    private static Retrofit createRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static HttpRequest getInstanceRequest() {
        if (httpRequest == null) {
            httpRequest = createRetrofit().create(HttpRequest.class);
        }
        return httpRequest;
    }

    public interface HttpRequest {
        @GET("json/movies.json")
        Call<List<MovieData>> getMovies();
    }

}
