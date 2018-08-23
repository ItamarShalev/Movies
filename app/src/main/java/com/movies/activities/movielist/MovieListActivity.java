package com.movies.activities.movielist;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.movies.R;
import com.movies.activities.qrscanner.ScanActivity;
import com.movies.adapter.AdapterMovieData;
import com.movies.data.MovieData;
import com.movies.dialog.ScannerDialog;
import com.movies.storage.SqliteMovieData;
import com.movies.utils.Global;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements MovieListMVP.View {

    private static final int REQUEST_CODE_QR_SCANNER = 23;
    /*
        private static final String KAY_FRAGMENTS = "KAY_FRAGMENTS";
    */
    private MovieListPresenter presenter;
    private RecyclerView movieListRecyclerView;
    private AdapterMovieData adapterMovieData;
    private ScannerDialog scannerDialog;
    private boolean isAfterOnSavedInstance;
    private Bundle bundleSaved;
    private boolean needShowDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_movie_list);
        presenter = new MovieListPresenter(this);
        presenter.init();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void initViews() {
        movieListRecyclerView = findViewById(R.id.movie_list_recycler_view);

        findViewById(R.id.qr_code_floating_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieListActivity.this, R.string.initializing_the_camera, Toast.LENGTH_SHORT).show();
                startScanning();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        bundleSaved = savedInstanceState;
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needShowDialog) {
            needShowDialog = false;
            Barcode barcode = bundleSaved.getParcelable(ScanActivity.TAG_QR_SCANNER);
            showDialogScanner(barcode);
        }
    }

    private void startScanning() {
        startActivityForResult(new Intent(getApplicationContext(), ScanActivity.class), REQUEST_CODE_QR_SCANNER);
    }

    @Override
    public ArrayList<MovieData> getAllMoviesFromDatabase() {
        return new SqliteMovieData(getApplicationContext()).getAllMoviesFromDataBase();
    }

    @Override
    public void initRecyclerWithMovies(final List<MovieData> movieDataList) {
        int sizeScreenHeight = Global.getHeightSizeScreen(this);
        int sizeScreenWidth = Global.getWidthSizeScreen(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.VERTICAL, false) {
        };

        if (Global.isPortraitOrientation(this)) {
            sizeScreenHeight = sizeScreenHeight / 2;
        } else {
            sizeScreenWidth = sizeScreenWidth / 2;
            gridLayoutManager.setSpanCount(2);
        }

        adapterMovieData = new AdapterMovieData(this, movieDataList, sizeScreenHeight, sizeScreenWidth);
        movieListRecyclerView.setHasFixedSize(true);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
        movieListRecyclerView.setAdapter(adapterMovieData);
    }

    @Override
    public void showDialogScanner(Barcode barcode) {
        scannerDialog = ScannerDialog.createInstance(barcode);
        scannerDialog.show(getSupportFragmentManager());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.bundleSaved = outState;
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_QR_SCANNER && resultCode == RESULT_OK) {
            Barcode barcode = data.getParcelableExtra(ScanActivity.TAG_QR_SCANNER);
            if (barcode != null) {
                bundleSaved.putParcelable(ScanActivity.TAG_QR_SCANNER, barcode);
                needShowDialog = true;
            }
        }
    }
}
