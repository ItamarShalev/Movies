package com.movies.activities.movielist;

import com.movies.data.MovieData;

import java.util.List;


class MovieListPresenter implements MovieListMVP.Presenter {

    private MovieListMVP.View view;

    MovieListPresenter(MovieListMVP.View view) {
        this.view = view;
    }


    @Override
    public void init() {
        view.initViews();
        List<MovieData> movieDataList = view.getAllMoviesFromDatabase();
        view.initRecyclerWithMovies(movieDataList);
    }
}
