package com.amazon.filmhero.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
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

public class FetchVideosTask extends AsyncTask<String, Void, Movie> {
    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String VIDEOS_PATH = "videos";
    private static final String API_KEY_PARAM = "api_key";

    private View rootView;
    private Movie movie;

    @BindView(R.id.genre)
    TextView videoListView;

    @BindView(R.id.runtime)
    TextView listItemVideoView;


    public FetchVideosTask(@NonNull View rootView, @NonNull Movie movie) {
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
                    .appendPath(VIDEOS_PATH)
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
            return getVideosFromJson(detailsJsonStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
    }

    private Movie getVideosFromJson(String detailsJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_SITE = "site";
        final String YOUTUBE = "YouTube";

        final JSONObject detailsJson = new JSONObject(detailsJsonStr);
        final JSONArray results = detailsJson.getJSONArray(TMDB_RESULTS);
        List<String> videos = new ArrayList<>();
        for(int i = 0; i < results.length(); i++) {
            // Get the JSON object representing the movie
            final JSONObject video = results.getJSONObject(i);
            if (video.getString(TMDB_SITE).equals(YOUTUBE)) {
                videos.add(video.getString(TMDB_KEY));
            }
        }

        movie.setVideos(videos);
        return movie;
    }
}
