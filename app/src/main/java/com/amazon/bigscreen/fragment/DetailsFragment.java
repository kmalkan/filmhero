package com.amazon.bigscreen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amazon.bigscreen.R;
import com.amazon.bigscreen.model.Movie;
import com.squareup.picasso.Picasso;

import static com.amazon.bigscreen.adapter.MovieAdapter.getImageUrl;


public class DetailsFragment extends Fragment {
    private Movie movie;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            movie = bundle.getParcelable("movie");
        }

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView moviePosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(getImageUrl(movie.getPosterPath())).into(moviePosterView);

        return rootView;
    }
}
