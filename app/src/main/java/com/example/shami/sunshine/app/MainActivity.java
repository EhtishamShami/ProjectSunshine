package com.example.shami.sunshine.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.example.shami.sunshine.app.data.WeatherContract;
import com.example.shami.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mLocation;
    private boolean mTwoPane;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private static final String LOG_TAG="[DM]Shami "+MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        mLocation = Utility.getPreferredLocation(this);
        Uri contentUri = getIntent() != null ? getIntent().getData() : null;
        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                DetailFragment fragment = new DetailFragment();
                             if (contentUri != null) {
                                   Bundle args = new Bundle();
                                    args.putParcelable(DetailFragment.DETAIL_URI, contentUri);
                                    fragment.setArguments(args);
                               }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                               .findFragmentById(R.id.fragment_forecast));
                forecastFragment.setUseTodayLayout(!mTwoPane);

        if (contentUri != null) {
                        forecastFragment.setInitialSelectedDate(WeatherContract.WeatherEntry.getDateFromUri(contentUri));
                    }

        SunshineSyncAdapter.initializeSyncAdapter(this);
/*
        if (checkPlayServices()) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);

            Log.i("RegIntentService",SENT_TOKEN_TO_SERVER);

            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                Toast.makeText(getApplicationContext(),"Here i am",Toast.LENGTH_SHORT).show();
                startService(intent);
            }
        }
        */

    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }

            return false;
        }
        return true;

    }

    @Override
        protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
             df.onLocationChanged(location);
             }

        }
    }

    @Override
    public void onItemSelected(Uri contentUri, ForecastAdapter.ForecastAdapterViewHolder vh) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                      new Pair<View, String>(vh.mIconView, getString(R.string.detail_icon_transition_name)));
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());

        }
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }




}







