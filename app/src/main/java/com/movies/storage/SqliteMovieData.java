package com.movies.storage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.movies.data.MovieData;
import com.movies.utils.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * Database for save movies with movie object
 */
public class SqliteMovieData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final String TABLE_NAME = "movies";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_IMAGE_URL = "ImageUrl";
    private static final String COLUMN_RATING = "Rating";
    private static final String COLUMN_RELEASE_YEAR = "ReleaseYear";
    private static final String COLUMN_GENRE = "Genre";
    private static int VERSION = 1;


    public SqliteMovieData(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_NAME + " TEXT PRIMARY KEY, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_RATING + " REAL, " +
                COLUMN_RELEASE_YEAR + " INTEGER, " +
                COLUMN_GENRE + " TEXT);";
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAll(List<MovieData> movieDataList) {
        if (movieDataList == null || movieDataList.size() == 0) {
            return;
        }

        SQLiteDatabase db = null;
        ContentValues values;
        try {
            db = getWritableDatabase();

            for (MovieData movie : movieDataList) {
                values = new ContentValues();
                values.put(COLUMN_NAME, movie.getName());
                values.put(COLUMN_IMAGE_URL, movie.getImageUrl());
                values.put(COLUMN_RATING, movie.getRating());
                values.put(COLUMN_RELEASE_YEAR, movie.getReleaseYear());
                values.put(COLUMN_GENRE, Global.convertListToString(movie.getGenre()));

                try {
                    db.insertOrThrow(TABLE_NAME, null, values);
                } catch (SQLException ignore) {
                }
            }
        } catch (Exception ignore) {
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public ArrayList<MovieData> getAllMoviesFromDataBase() {
        ArrayList<MovieData> listMovieData = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC;", TABLE_NAME, COLUMN_RELEASE_YEAR);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MovieData movieData = getMovieDataFromCursor(cursor);
                listMovieData.add(movieData);
                cursor.moveToNext();
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return listMovieData;
    }

    private MovieData getMovieDataFromCursor(Cursor cursor) {

        int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
        String name = cursor.getString(columnNameIndex);

        int columnImageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);
        String imageUrl = cursor.getString(columnImageUrlIndex);

        int columnRatingIndex = cursor.getColumnIndex(COLUMN_RATING);
        float rating = cursor.getFloat(columnRatingIndex);

        int columnReleaseYearIndex = cursor.getColumnIndex(COLUMN_RELEASE_YEAR);
        int releaseYear = cursor.getInt(columnReleaseYearIndex);

        int columnGenreIndex = cursor.getColumnIndex(COLUMN_GENRE);
        String genre = cursor.getString(columnGenreIndex);


        return new MovieData(name, imageUrl, rating, releaseYear, Global.convertStringToList(genre));
    }

    public boolean isEmpty() {
        return Global.isEmptyDB(getReadableDatabase(), TABLE_NAME);
    }


}
