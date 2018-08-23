package com.movies.activities.splash;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.movies.R;
import com.movies.activities.movielist.MovieListActivity;
import com.movies.data.MovieData;
import com.movies.dialog.BaseDialogFragment;
import com.movies.dialog.PositiveNegativeDialog;
import com.movies.storage.SqliteMovieData;
import com.movies.utils.Global;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements SplashMVP.View {

    PositiveNegativeDialog positiveNegativeDialog;
    private SplashPresenter presenter;
    private SqliteMovieData sqliteMovieData;
    private LottieAnimationView splashAnimation;
    private boolean isFirstTime = true;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_splash);
        sqliteMovieData = new SqliteMovieData(getApplicationContext());
        presenter = new SplashPresenter(this);
        presenter.init();
    }

    @Override
    public void initViews() {
        splashAnimation = findViewById(R.id.splash_animation);
    }

    @Override
    public void startAnimation() {
        if (isFirstTime) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (presenter != null) {
                        presenter.insertListAndMoveOn();
                    }
                }
            }, 2000);
            isFirstTime = false;
        }
        splashAnimation.setVisibility(View.VISIBLE);
        splashAnimation.playAnimation();
    }

    @Override
    public void finishSuccessfully() {
        stopAnimation();
    }

    private void stopAnimation() {
        splashAnimation.pauseAnimation();
        splashAnimation.setVisibility(View.GONE);
    }


    @Override
    public void insertMoviesToDatabase(List<MovieData> movieDataList) {
        sqliteMovieData.insertAll(movieDataList);
    }

    @Override
    public void moveToMovieListActivity() {
        startActivity(new Intent(getApplicationContext(), MovieListActivity.class));
        finish();
    }


    @Override
    public void errorHappened() {
        stopAnimation();
        if (BaseDialogFragment.findDialogFragment(getSupportFragmentManager()) != null || isDestroyed()) {
            return;
        }
        String bodyMessage = getString(R.string.fail_to_upload_check_networking);
        String tryAgainText = getString(R.string.try_again);
        View.OnClickListener listenerNegative = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.isOnline(getApplicationContext())) {
                    Toast.makeText(SplashActivity.this, R.string.not_yet_connection_yet, Toast.LENGTH_SHORT).show();
                } else {
                    startAnimation();
                    presenter.tryReadFromServer();
                    DialogFragment dialogFragment = (DialogFragment) v.getTag();
                    dialogFragment.dismiss();
                }
            }
        };
        if (sqliteMovieData.isEmpty()) {
            positiveNegativeDialog = createDialogOnlyNegative(bodyMessage, tryAgainText, listenerNegative);
        } else {
            bodyMessage += getString(R.string.continue_offline);
            String continueText = " " + getString(R.string.continue_text);
            View.OnClickListener listenerPositive = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToMovieListActivity();
                    DialogFragment dialogFragment = (DialogFragment) v.getTag();
                    dialogFragment.dismiss();
                }
            };
            positiveNegativeDialog = createDialog(bodyMessage, tryAgainText, continueText, listenerNegative, listenerPositive);

        }
        receiverToInternetOn();
        positiveNegativeDialog.show(getSupportFragmentManager());
    }

    private void receiverToInternetOn() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Global.isOnline(context)) {
                    if (!positiveNegativeDialog.isRemoving()) {
                        positiveNegativeDialog.dismiss();
                    }
                    presenter.tryReadFromServer();
                    unregisterReceiver(this);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private PositiveNegativeDialog createDialog(String bodyMessage, String negativeText, String positiveText, View.OnClickListener listenerNegative, View.OnClickListener listenerPositive) {
        String titleText = getString(R.string.connect_fail);
        return PositiveNegativeDialog.createInstance(titleText, bodyMessage, negativeText, positiveText, listenerNegative, listenerPositive);
    }

    private PositiveNegativeDialog createDialogOnlyNegative(String bodyMessage, String negativeText, View.OnClickListener listenerNegative) {
        return createDialog(bodyMessage, negativeText, "", listenerNegative, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.removeAllFragments(getSupportFragmentManager());
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        presenter.cancel();
        splashAnimation.cancelAnimation();
        presenter = null;
    }
}