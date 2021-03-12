package io.github.dipeshpatil.androidcrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
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

    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME,
                null
        );
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

    public void copyDatabase(File dbFile) {
        if (!dbFile.exists()) {
            try {
                InputStream is = context.getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                while (is.read(buffer) > 0) {
                    os.write(buffer);
                }

                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
    }
}