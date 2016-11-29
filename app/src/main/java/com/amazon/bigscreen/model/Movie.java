package com.amazon.bigscreen.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.amazon.bigscreen.adapter.MovieAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Movie implements Parcelable {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private String title;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private long popularity;
    private int voteCount;
    private long voteAverage;

    public Movie() {
    }

    public Movie(String title, String posterPath, String releaseDate, String overview,
                 long popularity, int voteCount, long voteAverage) {
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int i) {
        pc.writeString(title);
        pc.writeString(posterPath);
        pc.writeString(releaseDate);
        pc.writeString(overview);
        pc.writeLong(popularity);
        pc.writeInt(voteCount);
        pc.writeLong(voteAverage);
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
        title = pc.readString();
        posterPath = pc.readString();
        releaseDate = pc.readString();
        overview = pc.readString();
        popularity = pc.readLong();
        voteCount = pc.readInt();
        voteAverage = pc.readLong();
    }

    public Integer getYear() {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(df.parse(releaseDate));
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e(LOG_TAG, String.format("Error retrieving the year from the release date for" +
                    " Movie: %s", title));
            return null;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(long voteAverage) {
        this.voteAverage = voteAverage;
    }

}
