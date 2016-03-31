package com.akindo.popularmovies;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Main class for the Popular Movies app from the Udacity Google Android Developer Nanodegree.
 * https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true
 */
public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDevHelpers();

        // If The Movie DB API key is blank, show toast and then exit app.
        if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
            Toast toast = Toast.makeText(this, "Error, The Movie DB API key is blank, exiting app.",
                    Toast.LENGTH_SHORT);
            toast.show();

            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    finish();
                }
            };
            handler.postDelayed(r, 2000);

            Log.d(LOG_TAG, "Error, The Movie DB API key is blank, app was terminated.");
        }

        setContentView(R.layout.activity_main);
    }

    public void initDevHelpers() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

        LeakCanary.install(getApplication());

        if (BuildConfig.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems.
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.most_popular) {
            Toast toast = Toast.makeText(this, "Most popular.", Toast.LENGTH_SHORT);
            toast.show();
            Fragment frg = null;
            frg = getSupportFragmentManager().findFragmentByTag("activity_main");
            return true;
        } else if (id == R.id.highest_rated) {
            Toast toast = Toast.makeText(this, "Highest rated.", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

