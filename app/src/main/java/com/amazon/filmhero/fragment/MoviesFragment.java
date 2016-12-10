package com.amazon.filmhero.fragment;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amazon.filmhero.R;
import com.amazon.filmhero.activity.DetailActivity;
import com.amazon.filmhero.adapter.MovieAdapter;
import com.amazon.filmhero.async.FetchMoviesTask;
import com.amazon.filmhero.listener.EndlessGridScrollListener;
import com.amazon.filmhero.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MoviesFragment extends Fragment {
    // Sort criteria
    public static final String NOW_PLAYING = "now_playing";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String UPCOMING = "upcoming";

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.grid_movies)
    GridView gridView;

    private Unbinder unbinder;

    private MovieAdapter mMovieAdapter;
    private String sortCriteria = POPULAR;
    private int pageNumber = 1;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        gridView.setOnScrollListener(new EndlessGridScrollListener(5, pageNumber) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                pageNumber++;
                getMovies();
                return true;
            }
        });
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                final Intent detailIntent = new Intent(container.getContext(), DetailActivity.class);
                detailIntent.putExtra(Movie.EXTRA_TAG, mMovieAdapter.getItem(position));
                final Bundle bundle = ActivityOptions.makeCustomAnimation(
                        getContext(), R.anim.fade_in_zoom, R.anim.fade_out).toBundle();
                startActivity(detailIntent, bundle);
            }
        });

        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovieAdapter.isEmpty()) {
            getMovies();
        }
    }

    public void getMovies() {
        if (isOnline()) {
            new FetchMoviesTask(mMovieAdapter, sortCriteria.toString(), pageNumber).execute();
        } else {
            // Show internet connection error message
            Log.e(LOG_TAG, "Unable to connect to the internet.");
        }
    }

    public boolean isOnline() {
        final ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public MovieAdapter getmMovieAdapter() {
        return mMovieAdapter;
    }

    public void setmMovieAdapter(MovieAdapter mMovieAdapter) {
        this.mMovieAdapter = mMovieAdapter;
    }

    public String getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
