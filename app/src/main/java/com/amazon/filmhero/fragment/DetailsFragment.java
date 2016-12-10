package com.amazon.filmhero.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazon.filmhero.R;
import com.amazon.filmhero.activity.DetailActivity;
import com.amazon.filmhero.async.FetchDetailsTask;
import com.amazon.filmhero.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.amazon.filmhero.model.Movie.getImageUrl;

public class DetailsFragment extends Fragment {
    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private static final int BACKDROP_SIZE = 5;
    private static final int POSTER_SIZE = 4;
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final SparseIntArray ratingColorList = new SparseIntArray(10);
    static {
        ratingColorList.append(0, R.color.red_700);
        ratingColorList.append(1, R.color.red_700);
        ratingColorList.append(2, R.color.red_700);
        ratingColorList.append(3, R.color.red_500);
        ratingColorList.append(4, R.color.deepOrange_500);
        ratingColorList.append(5, R.color.orange_500);
        ratingColorList.append(6, R.color.amber_500);
        ratingColorList.append(7, R.color.lime_500);
        ratingColorList.append(8, R.color.lightGreen_500);
        ratingColorList.append(9, R.color.green_500);
        ratingColorList.append(10, R.color.green_500);
    }

    @BindView(R.id.details_scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.backdrop)
    ImageView backdropView;

    @BindView(R.id.movie_title)
    TextView movieTitleView;

    @BindView(R.id.movie_poster)
    ImageView moviePosterView;

    @BindView(R.id.poster_heart)
    ImageView posterHeartView;

    @BindView(R.id.vote_average)
    TextView voteAverageView;

    @BindView(R.id.overview)
    TextView overviewView;

    @BindView(R.id.favorite_icon)
    ImageView favoriteIconView;

    @BindView(R.id.share_icon)
    ImageView shareIconView;

    private View rootView;
    private Movie movie;
    private Unbinder unbinder;
    private AnimatorSet animatorSet;
    private GestureDetector detector;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        final Intent intent = getActivity().getIntent();

        unbinder = ButterKnife.bind(this, rootView);
        detector = new GestureDetector(getContext(), new MoviePosterGestureListener());

        // Animate the transparent to opaque effect as the user scrolls down.
        final ActionBar mActionBar = ((DetailActivity) getActivity()).getSupportActionBar();
        if (mActionBar != null) {
            final ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            cd.setAlpha(0);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setBackgroundDrawable(cd);

            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    cd.setAlpha(getAlpha(v.getScrollY()));
                }

                private int getAlpha(int scrollY) {
                    final int transparencyThreshold = 400;
                    if(scrollY > transparencyThreshold){
                        // opaque
                        return 255;
                    }
                    else if(scrollY < 0){
                        //transparent
                        return 0;
                    }
                    else {
                        backdropView.setTranslationY(-0.35f * scrollY);
                        return (int)((255.0 / transparencyThreshold) * scrollY);
                    }
                }
            });
        }

        if (intent != null && intent.hasExtra(Movie.EXTRA_TAG)) {
            movie = intent.getParcelableExtra(Movie.EXTRA_TAG);

            getDetails();

            Picasso.with(getContext())
                    .load(getImageUrl(movie.getBackdropPath(), BACKDROP_SIZE))
                    .placeholder(R.drawable.movie_backdrop_placeholder_w500)
                    .error(R.drawable.movie_backdrop_placeholder_w500)
                    .into(backdropView);

            movieTitleView.setText(String.format(Locale.ENGLISH, "%s (%d)", movie.getTitle(), movie.getYear()));

            Picasso.with(getContext())
                    .load(getImageUrl(movie.getPosterPath(), POSTER_SIZE))
                    .placeholder(R.drawable.movie_poster_placeholder_w500)
                    .error(R.drawable.movie_poster_placeholder_w500)
                    .into(moviePosterView);

            setMoviePosterListener();

            voteAverageView.setText(String.format(Locale.ENGLISH, "%.1f", movie.getVoteAverage()));
            voteAverageView.setBackgroundColor(ContextCompat.getColor(getContext(), ratingColorList.get(((int) movie.getVoteAverage()))));

            overviewView.setText(movie.getOverview());

            setFavoriteIconListener();
        }

        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getDetails() {
        if (isOnline()) {
            new FetchDetailsTask(rootView, movie).execute();
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

    private void toggleFavorite() {
        if (movie.isFavorite()) {
            favoriteIconView.setImageResource(R.drawable.ic_heart_border);
        } else {
            favoriteIconView.setImageResource(R.drawable.ic_heart_filled);
        }
        movie.setFavorite(!movie.isFavorite());
    }

    private void setMoviePosterListener() {
        animatorSet = new AnimatorSet();

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(posterHeartView, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(200);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(posterHeartView, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(200);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator pause = ObjectAnimator.ofInt(posterHeartView, "pause", 1, 1);
        pause.setDuration(200);
        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(posterHeartView, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(200);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(posterHeartView, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(200);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(pause).after(imgScaleUpYAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(pause);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset();
            }
        });

        moviePosterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }

    private void setFavoriteIconListener() {
        favoriteIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite();
            }
        });
    }

    private void heart() {
        posterHeartView.setVisibility(View.VISIBLE);

        posterHeartView.setScaleY(0.1f);
        posterHeartView.setScaleX(0.1f);

        animatorSet.start();

        if (!movie.isFavorite()) {
            toggleFavorite();
        }
    }

    public void reset() {
        if (posterHeartView != null) {
            posterHeartView.setVisibility(View.GONE);
        }
    }

    public class MoviePosterGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            heart();
            return true;
        }
    }

}
