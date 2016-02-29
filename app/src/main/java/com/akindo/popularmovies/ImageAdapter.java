package com.akindo.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.akindo.popularmovies.movie.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An adapter to hold movie poster images.
 */
public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
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

        LinearLayout linearLayout;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            linearLayout = (LinearLayout) inflater.inflate(R.layout.movie_poster_image_view, parent, false);
        } else {
            linearLayout = (LinearLayout) convertView;
        }

        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.poster_view);

        Uri posterUri = movie.buildPosterUri(mContext.getString(R.string.the_movie_db_poster_size));
        Log.d(LOG_TAG, posterUri.toString());

        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

//        int width= mContext.getResources().getDisplayMetrics().widthPixels;
//        com.squareup.picasso.Picasso
//                .with(mContext)
//                .load("some url")
//                .centerCrop().resize(width/2,width/2)
//                .error(R.drawable.sample_0)
//                .placeholder(R.drawable.sample_0)
//                .into(imageView);

        return linearLayout;
    }
}
