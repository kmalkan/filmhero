package com.amazon.bigscreen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazon.bigscreen.R;
import com.amazon.bigscreen.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private static final String UNKNOWN = "Unknown";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String[] IMAGE_SIZE = {
            "w92", "w154", "w185", "w342", "w500", "w780", "original"
    };


    private Context mContext;
    private List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
        this.mContext = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        } else {
            v = convertView;
        }

        Movie movie = getItem(position);

        ImageView moviePosterView = (ImageView) v.findViewById(R.id.list_item_movie_poster);
        moviePosterView.setLayoutParams(new GridView.LayoutParams(300, 500));
        moviePosterView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        moviePosterView.setPadding(10, 10, 10, 10);
        assert movie != null;
        Picasso.with(getContext()).load(getImageUrl(movie.getPosterPath())).into(moviePosterView);

        return v;
    }

    public static String getImageUrl(String path) {
        return IMAGE_BASE_URL + IMAGE_SIZE[2] + path;
    }
}
