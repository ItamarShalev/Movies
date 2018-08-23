package com.movies.activities.splash;

import com.movies.data.MovieData;
import com.movies.server.HttpServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashPresenter implements SplashMVP.Presenter {
    private SplashMVP.View view;
    private Call<List<MovieData>> callMovies;
    private List<MovieData> movieDataList;
    private boolean isFirstTry = true;
    private boolean isCanceled;

    SplashPresenter(SplashMVP.View view) {
        this.view = view;
    }


    @Override
    public void init() {
        view.initViews();
        tryReadFromServer();
    }


    private void readMoviesFromServer() {
        callMovies = HttpServer.getInstanceRequest().getMovies();
        callMovies.enqueue(new Callback<List<MovieData>>() {
            @Override
            public void onResponse(Call<List<MovieData>> call, Response<List<MovieData>> response) {
                if (!isCanceled) {
                    if (response.isSuccessful()) {
                        movieDataList = response.body();
                        if (!isFirstTry) {
                            insertListAndMoveOn();
                        }
                    } else {
                        view.errorHappened();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MovieData>> call, Throwable t) {
                if (!isCanceled) {
                    view.errorHappened();
                }
            }
        });
    }

    @Override
    public void cancel() {
        isCanceled = true;
        if (callMovies != null) {
            callMovies.cancel();
        }
        view = null;
    }

    public void insertListAndMoveOn() {
        if (movieDataList != null && view != null && !isCanceled) {
            view.insertMoviesToDatabase(movieDataList);
            callMovies = null;
            view.finishSuccessfully();
            view.moveToMovieListActivity();
        } else {
            isFirstTry = false;
        }
    }


    @Override
    public void tryReadFromServer() {
        view.startAnimation();
        readMoviesFromServer();
    }


}
