package com.amazon.bigscreen.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.amazon.bigscreen.BuildConfig;
import com.amazon.bigscreen.adapter.MovieAdapter;
import com.amazon.bigscreen.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH  = "popular";
    private static final String API_KEY_PARAM = "api_key";

    private MovieAdapter mMovieAdapter;

    public FetchMoviesTask(MovieAdapter mMovieAdapter) {
        this.mMovieAdapter = mMovieAdapter;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        try {

            Uri uri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(POPULAR_PATH)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                    .build();

            // Create the request to TMDB, and open the connection
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            final InputStream inputStream = urlConnection.getInputStream();
            final StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
        }
    }

    private List<Movie> getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray results = moviesJson.getJSONArray(TMDB_RESULTS);
        List<Movie> movies = new ArrayList<>();

        for(int i = 0; i < results.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = results.getJSONObject(i);

            // Create a new Movie object
            Movie movie = new Movie();

            movie.setTitle(movieJson.getString(TMDB_TITLE));
            movie.setPosterPath(movieJson.getString(TMDB_POSTER_PATH));
            movie.setReleaseDate(movieJson.getString(TMDB_RELEASE_DATE));
            movie.setOverview(movieJson.getString(TMDB_OVERVIEW));
            movie.setPopularity(movieJson.getLong(TMDB_POPULARITY));
            movie.setVoteCount(movieJson.getInt(TMDB_VOTE_COUNT));
            movie.setVoteAverage(movieJson.getLong(TMDB_VOTE_AVERAGE));

            movies.add(movie);
        }

        return movies;
    }
}