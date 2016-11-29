package com.amazon.bigscreen.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.amazon.bigscreen.R;
import com.amazon.bigscreen.adapter.MovieAdapter;
import com.amazon.bigscreen.async.FetchMoviesTask;
import com.amazon.bigscreen.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private MovieAdapter mMovieAdapter;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.grid_movies);
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        gridview.setAdapter(mMovieAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Fragment detailsFragment = new DetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("movie", mMovieAdapter.getItem(position));
                detailsFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main, detailsFragment)
                        .addToBackStack("Movies")
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovies(mMovieAdapter);
    }

    public void getMovies(MovieAdapter mMovieAdapter) {
        new FetchMoviesTask(mMovieAdapter).execute();
    }
}
