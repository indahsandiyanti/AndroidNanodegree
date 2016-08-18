package com.nanodegreeandroid.com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by indah on 16/8/16.
 */
public class Movie implements Parcelable {
    public static final String EXTRA_DETAIL = "com.nanodegreeandroid.com.popularmovies.details";

    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private String imagePath;
    private double voteAverage;

    public Movie(int id, String title, String overview, String releaseDate,
                 String imagePath, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
        this.voteAverage = voteAverage;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        imagePath = in.readString();
        voteAverage = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(imagePath);
        parcel.writeDouble(voteAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel)
        {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

}
