package com.amazon.filmhero.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Movie implements Parcelable {
    public static final String EXTRA_TAG= "movie";
    private static final String LOG_TAG = Movie.class.getSimpleName();

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String[] IMAGE_SIZE = {
            "w92", "w154", "w185", "w342", "w500", "w780", "original"
    };
    private int id;
    private String title;
    private String backdropPath;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private long popularity;
    private int voteCount;
    private double voteAverage;
    private List<String> genres;
    private int runtime;
    private List<String> videos;
    private boolean isFavorite;

    public Movie() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int i) {
        pc.writeInt(id);
        pc.writeString(title);
        pc.writeString(backdropPath);
        pc.writeString(posterPath);
        pc.writeString(releaseDate);
        pc.writeString(overview);
        pc.writeLong(popularity);
        pc.writeInt(voteCount);
        pc.writeDouble(voteAverage);
        pc.writeList(genres);
        pc.writeInt(runtime);
        pc.writeList(videos);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel pc){
        id = pc.readInt();
        title = pc.readString();
        backdropPath = pc.readString();
        posterPath = pc.readString();
        releaseDate = pc.readString();
        overview = pc.readString();
        popularity = pc.readLong();
        voteCount = pc.readInt();
        voteAverage = pc.readDouble();
        pc.readList(genres, List.class.getClassLoader());
        runtime = pc.readInt();
        pc.readList(videos, List.class.getClassLoader());
    }

    public static String getImageUrl(String path, int size) {
        return IMAGE_BASE_URL + IMAGE_SIZE[size] + path;
    }

    public Integer getYear() {
        final DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        final Calendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(df.parse(releaseDate));
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e(LOG_TAG, String.format("Error retrieving the year from the release date for" +
                    " Movie: %s", title));
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public long getPopularity() {
        return popularity;
    }

    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
