package com.amazon.filmhero.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazon.filmhero.BuildConfig;
import com.amazon.filmhero.R;
import com.amazon.filmhero.model.Movie;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FetchDetailsTask extends AsyncTask<String, Void, Movie> {
    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAM = "api_key";

    private View rootView;
    private Movie movie;

    @BindView(R.id.genre)
    TextView genreView;

    @BindView(R.id.runtime)
    TextView runtimeView;


    public FetchDetailsTask(@NonNull View rootView, @NonNull Movie movie) {
        this.rootView = rootView;
        this.movie = movie;
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected Movie doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String detailsJsonStr = null;

        try {
            final Uri uri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(movie.getId()))
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                    .build();

            // Create the request to TMDB, and open the connection
            final URL url = new URL(uri.toString());

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
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            detailsJsonStr = buffer.toString();
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
            return getDetailsDataFromJson(detailsJsonStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String genre : movie.getGenres()) {
            sb.append(delim).append(genre);
            delim = ", ";
        }
        genreView.setText(sb.toString());
        runtimeView.setText(String.valueOf(movie.getRuntime()));
    }

    private Movie getDetailsDataFromJson(String detailsJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_GENRES = "genres";
        final String TMDB_NAME = "name";
        final String TMDB_RUNTIME = "runtime";

        final JSONObject detailsJson = new JSONObject(detailsJsonStr);
        final JSONArray genresJson = detailsJson.getJSONArray(TMDB_GENRES);
        final List<String> genres = new ArrayList<>();

        for (int i =0; i < genresJson.length(); i++) {
            // Get the JSON object representing the genre
            final JSONObject genre = genresJson.getJSONObject(i);
            genres.add(genre.getString(TMDB_NAME));
        }

        movie.setGenres(genres);
        movie.setRuntime(detailsJson.getInt(TMDB_RUNTIME));

        return movie;
    }
}
