package com.movies.activities.moviedetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.movies.R;
import com.movies.data.MovieData;
import com.movies.dialog.ImageZoomDialog;
import com.movies.utils.Global;
import com.movies.utils.ImageHandler;


/**
 * This activity show all details from movie data , just put the movie to intent with tag #TAG_MOVIE_DATA
 */
public class MovieDetailsActivity extends AppCompatActivity {

    public static final String TAG_MOVIE_DATA = "TAG_MOVIE_DATA";
    private ImageView imageMovieImageView;
    private ImageHandler imageHandler;
    private MovieData movieData;
    private TextView nameMovieTextView;
    private RatingBar ratingBar;
    private TextView releaseYearTextView;
    private TextView ratingValueTextView;
    private TextView genreTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_movie_details);
        initViews();
        initListeners();
        initObjects();
        initMovieDetailsToView(movieData);
    }


    public void initViews() {
        imageMovieImageView = findViewById(R.id.image_movie_image_view);
        nameMovieTextView = findViewById(R.id.name_movie_text_view);
        ratingBar = findViewById(R.id.rating_bar);
        releaseYearTextView = findViewById(R.id.release_year_text_view);
        ratingValueTextView = findViewById(R.id.rating_value_text_view);
        genreTextView = findViewById(R.id.genre_text_view);
    }

    private void initListeners() {
        imageMovieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageZoomDialog imageZoomDialog = ImageZoomDialog.createInstance(movieData.getImageUrl(), movieData.getName());
                imageZoomDialog.show(getSupportFragmentManager());
            }
        });
    }

    private void initObjects() {
        imageHandler = new ImageHandler(getFilesDir().getPath());
        movieData = getIntent().getParcelableExtra(TAG_MOVIE_DATA);
    }


    private void initMovieDetailsToView(MovieData movieData) {
        imageMovieImageView.setImageBitmap(imageHandler.readImageFromStorage(imageHandler.getNameFromUrl(movieData.getImageUrl())));
        imageMovieImageView.setAdjustViewBounds(true);

        nameMovieTextView.setText(movieData.getName());
        releaseYearTextView.setText(("Release year : " + movieData.getReleaseYear()));
        ratingBar.setRating(movieData.getRating());
        ratingValueTextView.setText(("(" + movieData.getRating() + ")"));

        String genreString = "Genre : " + Global.convertListToString(movieData.getGenre(), ", ");
        genreTextView.setText(genreString);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
