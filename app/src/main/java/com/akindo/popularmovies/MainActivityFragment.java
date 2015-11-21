package com.akindo.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String baseURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
    public static final String baseImageURL = "http://image.tmdb.org/t/p/";
    public static final String[] imageSizes = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    public static final String phoneSize = "w185";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //String APIKey = readAPIKeyFromConfig();
        //getPopularMovies(APIKey);

        GridView gridview = (GridView) rootView.findViewById(R.id.movie_posters_gridview);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        return rootView;

        /*gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(HelloGridView.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }*
        });*/
    }

    private String readAPIKeyFromConfig() {
        //// TODO: 21/11/2015
        return null;
    }

    private void getPopularMovies(String APIKey) {
        //// TODO: 21/11/2015
        //baseURL + APIKey
    }

    private void getPopularMovieIDs() {

    }


}
