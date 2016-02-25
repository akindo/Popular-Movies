package com.akindo.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Fragment displaying a list of popular movies.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public static final String baseURL = "http://api.themoviedb.org/3/discover/movie?" +
            "sort_by=popularity.desc&api_key=";
    public static final String baseImageURL = "http://image.tmdb.org/t/p/";
    public static final String[] imageSizes = {"w92", "w154", "w185", "w342", "w500", "w780",
            "original"};
    public static final String phoneSize = "w185";

    enum SortOrder {
        MOST_POPULAR, HIGHEST_RATED
    }

    private SortOrder sortOrder = SortOrder.MOST_POPULAR;

    public static final int MAX_PAGES = 100;
    private boolean mIsLoading = false;
    private int mPagesLoaded = 0;
    private TextView mLoading;
    private ImageAdapter mImageAdapter;

    public MainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mImageAdapter = new ImageAdapter(getActivity());

//        GridView gridview = (GridView) rootView.findViewById(R.id.movie_posters_gridview);
//        gridview.setAdapter(mImageAdapter);
//
//        new FetchPopularMoviesTask().execute();
        initGrid(rootView);

        return rootView;
    }

    private void initGrid(View view) {
        GridView gridview = (GridView) view.findViewById(R.id.movie_posters_gridview);

        if (gridview == null) {
            return;
        }

        gridview.setAdapter(mImageAdapter);

        gridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {}

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int
                            visibleItemCount, int totalItemCount) {
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
            sortOrder = SortOrder.MOST_POPULAR;
            Toast toast = Toast.makeText(getActivity(), "Most popular.", Toast.LENGTH_SHORT);
            toast.show();
            new FetchPopularMoviesTask().execute();
            return true;
        } else if (id == R.id.highest_rated) {
            sortOrder = SortOrder.HIGHEST_RATED;
            Toast toast = Toast.makeText(getActivity(), "Highest rated.", Toast.LENGTH_SHORT);
            toast.show();
            new FetchPopularMoviesTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchPopularMoviesTask extends AsyncTask<Integer, Void, Collection<Movie>> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        // Call The Movie DB API to get a JSON response of popular movies.
        @Override
        protected Collection<Movie> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            int page = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

                String sortOrderString;
                if (sortOrder == SortOrder.MOST_POPULAR) {
                    sortOrderString = "popularity.desc";
                } else {
                    sortOrderString = "vote_average.desc";
                }

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", sortOrderString)
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

                stopLoading();
                return;
            }

            mPagesLoaded++;

            stopLoading();
//            if (mImageAdapter.getCount() > 0) {
//                mImageAdapter.clear();
//            }

            // Add new data from the server to the image adapter.
            mImageAdapter.addAll(results);
        }
    }
}
