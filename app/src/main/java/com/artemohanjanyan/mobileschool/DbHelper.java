package com.artemohanjanyan.mobileschool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "artists.db";

    public static final String TABLE_NAME = "artists";
    public static final String
            ID = "_ID",
            NAME = "NAME",
            GENRES = "GENRES",
            TRACKS = "TRACKS",
            ALBUMS = "ALBUMS",
            LINK = "LINK",
            DESCRIPTION = "DESCRIPTION",
            SMALL_COVER = "SMALL_COVER",
            BIG_COVER = "BIG_COVER",
            NAME_LOWER = "NAME_LOWER",
            DESCRIPTION_LOWER = "DESCRIPTION_LOWER";

    /**
     * Genres in GENRES field in the database are separated by this string.
     */
    public static final String DELIMITER = "$";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "db created");
        db.execSQL("CREATE VIRTUAL TABLE " + TABLE_NAME + " USING FTS3 (" +
                ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT," +
                GENRES + " TEXT," +
                TRACKS + " INTEGER," +
                ALBUMS + " INTEGER," +
                LINK + " TEXT," +
                DESCRIPTION + " TEXT," +
                SMALL_COVER + " TEXT," +
                BIG_COVER + " TEXT," +
                NAME_LOWER + " TEXT," +
                DESCRIPTION_LOWER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
