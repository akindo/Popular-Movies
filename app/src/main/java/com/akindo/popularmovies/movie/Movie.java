package com.akindo.popularmovies.movie;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a movie returned from themoviedb.org.
 */
public class Movie {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String OVERVIEW = "overview";
    public static final String POSTER_PATH = "poster_path";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String VOTE_COUNT = "vote_count";
    public static final String RELEASE_DATE = "release_date";

    public final long id;
    public final String title;
    public final String overview;
    public final String poster_path;
    public final double vote_average;
    public final long vote_count;
    public final String release_date;

    public Movie(long id, String title, String overview, String poster_path,
                 double vote_average, long vote_count, String release_date) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
    }

    public static Movie fromJson(JSONObject jsonObject) throws JSONException {
        return new Movie(
                jsonObject.getLong(ID),
                jsonObject.getString(TITLE),
                jsonObject.getString(OVERVIEW),
                jsonObject.getString(POSTER_PATH),
                jsonObject.getDouble(VOTE_AVERAGE),
                jsonObject.getLong(VOTE_COUNT),
                jsonObject.getString(RELEASE_DATE)
        );
    }

    public Uri buildPosterUri(String posterSize) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri posterURI = Uri.parse(BASE_URL).buildUpon()
                .appendPath(posterSize)
                .appendEncodedPath(poster_path)
                .build();

        return posterURI;
    }
}