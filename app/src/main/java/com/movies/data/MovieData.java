package com.movies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * object data for movie contains name,imageUrl,rating,releaseYear,genre
 */
public class MovieData implements Parcelable {


    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
    @SerializedName("title")
    private String name;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("rating")
    private float rating;
    @SerializedName("releaseYear")
    private int releaseYear;
    @SerializedName("genre")
    private List<String> genre;

    public MovieData() {
    }

    public MovieData(String name, String imageUrl, float rating, int releaseYear, List<String> genre) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    protected MovieData(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        rating = in.readFloat();
        releaseYear = in.readInt();
        genre = in.createStringArrayList();
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public List<String> getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeFloat(rating);
        dest.writeInt(releaseYear);
        dest.writeStringList(genre);
    }


}