package io.github.dipeshpatil.androidcrud.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import io.github.dipeshpatil.androidcrud.Movies.MovieItem;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int BY_ID_ASC = 0;
    public static final int BY_ID_DESC = 1;
    public static final int BY_TITLE_ASC = 2;
    public static final int BY_TITLE_DESC = 3;
    public static final int BY_RATING_ASC = 4;
    public static final int BY_RATING_DESC = 5;
    public static final int BY_YEAR_ASC = 6;
    public static final int BY_YEAR_DESC = 7;

    public static final String DB_NAME = "Movies.db";
    public static final String TABLE_NAME = "movies";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "title";
    public static final String COL_3 = "plot";
    public static final String COL_4 = "rating";
    public static final String COL_5 = "poster";
    public static final String COL_6 = "genre";
    public static final String COL_7 = "year";
    public static final String COL_8 = "released";
    public static final String COL_9 = "actors";
    public static final String COL_10 = "director";
    public static final String COL_11 = "title_slug";
    public Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                COL_8 + " TEXT, " +
                COL_9 + " TEXT, " +
                COL_10 + " TEXT, " +
                COL_11 + " TEXT " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public int getCount() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME,
                null
        ).getCount();
    }

    public boolean insertData(@NonNull MovieItem item) {
        return insertData(
                item.getTitle(),
                item.getPlot(),
                item.getRating(),
                item.getPoster(),
                item.getGenre(),
                item.getYear(),
                item.getReleased(),
                item.getActors(),
                item.getDirectors(),
                item.getTitle_slug()
        );
    }

    public boolean insertData(
            String title,
            String plot,
            String rating,
            String poster,
            String genre,
            String year,
            String released,
            String actors,
            String directors,
            String title_slug
    ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, title);
        contentValues.put(COL_3, plot);
        contentValues.put(COL_4, rating);
        contentValues.put(COL_5, poster);
        contentValues.put(COL_6, genre);
        contentValues.put(COL_7, year);
        contentValues.put(COL_8, released);
        contentValues.put(COL_9, actors);
        contentValues.put(COL_10, directors);
        contentValues.put(COL_11, title_slug);

        long result = sqLiteDatabase.insert(
                TABLE_NAME,
                null,
                contentValues
        );

        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getAllData(int choice) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String QUERY = "SELECT * FROM " + TABLE_NAME;

        switch (choice) {
            case BY_ID_ASC:
                QUERY += " ORDER BY " + COL_1 + " ASC";
                break;
            case BY_ID_DESC:
                QUERY += " ORDER BY " + COL_1 + " DESC";
                break;
            case BY_TITLE_ASC:
                QUERY += " ORDER BY " + COL_2 + " ASC";
                break;
            case BY_TITLE_DESC:
                QUERY += " ORDER BY " + COL_2 + " DESC";
                break;
            case BY_RATING_ASC:
                QUERY += " ORDER BY " + COL_4 + " ASC";
                break;
            case BY_RATING_DESC:
                QUERY += " ORDER BY " + COL_4 + " DESC";
                break;
            case BY_YEAR_ASC:
                QUERY += " ORDER BY " + COL_7 + " ASC";
                break;
            case BY_YEAR_DESC:
                QUERY += " ORDER BY " + COL_7 + " DESC";
        }

        return sqLiteDatabase.rawQuery(QUERY, null);
    }

    public Cursor getDataByTitle(String title) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE title = ?",
                new String[]{title}
        );
    }

    public boolean deleteDataByTitle(String title) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(
                TABLE_NAME,
                COL_2 + " = ?",
                new String[]{title}
        );
        return true;
    }

    public boolean alreadyExists(String title_slug) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(
                "SELECT title_slug FROM " + TABLE_NAME + " WHERE title_slug = ?",
                new String[]{title_slug}
        ).getCount() != 0;
    }

    public boolean updateData(
            String id,
            String title,
            String plot,
            String rating,
            String poster,
            String genre,
            String year,
            String released,
            String actors,
            String directors,
            String title_slug
    ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, title);
        contentValues.put(COL_3, plot);
        contentValues.put(COL_4, rating);
        contentValues.put(COL_5, poster);
        contentValues.put(COL_6, genre);
        contentValues.put(COL_7, year);
        contentValues.put(COL_8, released);
        contentValues.put(COL_9, actors);
        contentValues.put(COL_10, directors);
        contentValues.put(COL_11, title_slug);

        sqLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "ID = ?",
                new String[]{id}
        );

        return true;
    }

    public boolean deleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(
                TABLE_NAME,
                "ID = ?",
                new String[]{id}
        );
        return true;
    }
}