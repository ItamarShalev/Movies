package com.movies.activities.movielist;

import com.google.android.gms.vision.barcode.Barcode;
import com.movies.data.MovieData;

import java.util.List;


interface MovieListMVP {

    interface View {
        void initViews();

        List<MovieData> getAllMoviesFromDatabase();

        void initRecyclerWithMovies(List<MovieData> movieDataList);

        void showDialogScanner(Barcode barcode);
    }

    interface Presenter {
        void init();

    }
}
