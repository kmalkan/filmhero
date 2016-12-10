package com.amazon.bigscreen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.amazon.bigscreen.R;
import com.amazon.bigscreen.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amazon.bigscreen.model.Movie.getImageUrl;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final int POSTER_SIZE = 2;

    private List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
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
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ButterKnife.bind(this, view);

        final Movie movie = getItem(position);

        holder.moviePosterView.setLayoutParams(new GridView.LayoutParams(300, 500));
        holder.moviePosterView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.moviePosterView.setPadding(10, 10, 10, 10);

        if (movie != null) {
            Picasso.with(getContext())
                    .load(getImageUrl(movie.getPosterPath(), POSTER_SIZE))
                    .placeholder(R.drawable.movie_poster_placeholder_w185)
                    .into(holder.moviePosterView);
        }

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.list_item_movie_poster)
        ImageView moviePosterView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
