package com.akindo.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.akindo.popularmovies.movie.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Fragment displaying a list of popular movies.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MOST_POPULAR, HIGHEST_RATED})
    public @interface SortOrder {}

    public static final int MOST_POPULAR = 0;
    public static final int HIGHEST_RATED = 1;
    @SortOrder int sortOrder = MOST_POPULAR;

    public static final int MAX_PAGES = 100;
    private boolean mIsLoading = false;
    private int mPagesLoaded = 0;
    private TextView mLoading;
    private ImageAdapter mImageAdapter;

    public MainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter = new ImageAdapter(getActivity());
        mLoading = (TextView) view.findViewById(R.id.loading);
        initGrid(view);
        startLoading();

        return view;
    }

    private void initGrid(View view) {
        GridView gridview = (GridView) view.findViewById(R.id.movie_posters_gridview);

        if (gridview == null) {
            return;
        }

        gridview.setAdapter(mImageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v,
                                    int position,
                                    long id) {

                ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
                Movie movie = adapter.getItem(position);

                if (movie == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
                getActivity().startActivity(intent);
                //startActivity(intent);
            }
        });

        gridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == totalItemCount) {
                            startLoading();
                        }
                    }
                }
        );
    }

    private void startLoading() {
        if (mIsLoading) {
            return;
        }

        if (mPagesLoaded >= MAX_PAGES) {
            return;
        }

        mIsLoading = true;

        if (mLoading != null) {
            mLoading.setVisibility(View.VISIBLE);
        }

        new FetchPopularMoviesTask().execute(mPagesLoaded + 1);
    }

    private void stopLoading() {
        if (!mIsLoading) {
            return;
        }

        mIsLoading = false;

        if (mLoading != null) {
            mLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.most_popular) {
            sortOrder = MOST_POPULAR;
            mImageAdapter.clear();
            mPagesLoaded = 0;
            new FetchPopularMoviesTask().execute(mPagesLoaded + 1);
            return true;
        } else if (id == R.id.highest_rated) {
            sortOrder = HIGHEST_RATED;
            mImageAdapter.clear();
            mPagesLoaded = 0;
            startLoading();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchPopularMoviesTask extends AsyncTask<Integer, Void, Collection<Movie>> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        // Call The Movie DB API to get a JSON response of popular movies.
        @Override
        protected Collection<Movie> doInBackground(Integer... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            if (params.length == 0) {
                return null;
            }

            int page = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                String API_BASE_URL = "http://api.themoviedb.org/3/movie/";

                String sortOrderString;
                if (sortOrder == MOST_POPULAR) {
                    sortOrderString = "popular";
                } else {
                    sortOrderString = "top_rated";
                }

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(sortOrderString)
                        .appendQueryParameter("page", String.valueOf(page))
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                Log.d(LOG_TAG, "The Movie DB query URI: " + builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to The Movie DB, and open the connection.
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String.
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }

                responseJsonStr = buffer.toString();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return fetchMoviesFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        private Collection<Movie> fetchMoviesFromJson(String jsonStr) throws JSONException {
            final String MOVIES = "results";

            JSONObject JSON  = new JSONObject(jsonStr);
            JSONArray movies = JSON.getJSONArray(MOVIES);
            ArrayList result = new ArrayList<>();

            for (int i = 0; i < movies.length(); i++) {
                result.add(Movie.fromJson(movies.getJSONObject(i)));
            }

            return result;
        }

        @Override
        protected void onPostExecute(Collection<Movie> results) {
            // Show an error toast if there are no results.
            if (results == null) {
                Toast.makeText(
                        getActivity(),
                        "Error fetching movies from The Movie DB.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            mPagesLoaded++;
            stopLoading();
            // Add new data from the server to the image adapter.
            mImageAdapter.addAll(results);
        }
    }
}
