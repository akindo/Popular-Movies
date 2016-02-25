package com.akindo.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.akindo.popularmovies.movie.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An adapter to hold movie poster images.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private final ArrayList<Movie> mMovies;

    public ImageAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<>();
    }

    public void addAll(Collection<Movie> movies) {
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void clear() {
        mMovies.clear();
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        if (position < 0 || position >= mMovies.size()) {
            return null;
        }
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        Movie movie = getItem(position);

        if (movie == null) {
            return -1L;
        }

        return movie.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (movie == null) {
            return null;
        }

        ImageView imageView;

        if (convertView == null) {
            // If the view isn't recycled, initialise some attributes.
            imageView = new ImageView(mContext);

            //imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            //imageView.setLayoutParams(new GridView.LayoutParams(700, 700));
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Crop the images such that they take up the whole space, i. e. no white space around
            // them.
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            // Recycle the view.
            imageView = (ImageView) convertView;
        }

        Uri posterUri = movie.buildPosterUri(mContext.getString(R.string.the_movie_db_poster_size));
        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

        return imageView;
    }
}
