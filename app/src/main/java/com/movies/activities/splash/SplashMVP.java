package com.movies.activities.splash;

import com.movies.data.MovieData;

import java.util.List;


public interface SplashMVP {

    interface View {
        void initViews();

        void startAnimation();

        void finishSuccessfully();

        void insertMoviesToDatabase(List<MovieData> movieDataList);

        void moveToMovieListActivity();

        void errorHappened();
    }

    interface Presenter {
        void init();

        void cancel();

        void tryReadFromServer();

        void insertListAndMoveOn();
    }
}
