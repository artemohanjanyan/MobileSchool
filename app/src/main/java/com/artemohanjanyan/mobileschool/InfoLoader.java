package com.artemohanjanyan.mobileschool;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads description of artists asynchronously.
 */
public class InfoLoader extends AsyncTaskLoader<Cursor> {

    public static final String REFRESH_EXTRA = "refresh";
    public static final String SEARCH_EXTRA = "search";

    private static final String TAG = InfoLoader.class.getSimpleName();
    private static final String jsonURL = "http://cache-spb03.cdn.yandex.net/" +
            "download.cdn.yandex.net/mobilization-2016/artists.json";
    private SQLiteDatabase db;
    private Cursor cursor;
    private volatile boolean shouldRefresh = false;
    private volatile String searchString = "";

    /**
     * Creates loader.
     * @param context context
     * @param args if it contains true at {@link InfoLoader#REFRESH_EXTRA},
     *             then all the data will be downloaded from the Internet, independently of caching.
     */
    public InfoLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            shouldRefresh = args.getBoolean(REFRESH_EXTRA, false);
            searchString = args.getString(SEARCH_EXTRA, "");
        }
    }

    /**
     * Loads information about artists.
     * May download data from the web, depending on request and cache availability.
     * @return cursor pointing to information, or null if some error happens. See {@link DbHelper}.
     */
    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "load started");

        DbHelper dbHelper = new DbHelper(getContext());

        if (shouldRefresh) {
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                if (!download(db)) {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        if (isLoadInBackgroundCanceled()) {
            return null;
        }

        db = dbHelper.getReadableDatabase();
        return db.query(DbHelper.TABLE_NAME, null,
                searchString.equals("") ? null : DbHelper.TABLE_NAME + " MATCH ?",
                searchString.equals("") ? null : new String[]{searchString},
                null, null, null);
    }

    @Override
    protected void onStartLoading() {
        if (cursor == null) {
            if (db != null) {
                db.close();
                db = null;
            }
            forceLoad();
        } else {
            deliverResult(cursor);
        }
        super.onStartLoading();
    }

    @Override
    public void deliverResult(Cursor data) {
        Log.d(TAG, "deliverResult");
        cursor = data;
        super.deliverResult(data);
    }

    @Override
    public void onCanceled(Cursor data) {
        Log.d(TAG, "onCanceled");
        cursor = data;
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        super.onReset();
    }

    /**
     * Downloads JSON from the Internet, parses it and writes parsed data to database.
     * @return true on success, false otherwise.
     * @throws IOException if some IO error happens.
     */
    private boolean download(SQLiteDatabase db) throws IOException {
        try (
                    JsonReader reader = new JsonReader(new InputStreamReader(
                        new URL(jsonURL).openStream(), "UTF-8"))) {
            SQLiteStatement statement = db.compileStatement(String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                            "VALUES (?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ? )",
                    DbHelper.TABLE_NAME,
                    DbHelper.ID,
                    DbHelper.NAME,
                    DbHelper.GENRES,
                    DbHelper.TRACKS,
                    DbHelper.ALBUMS,
                    DbHelper.LINK,
                    DbHelper.DESCRIPTION,
                    DbHelper.SMALL_COVER,
                    DbHelper.BIG_COVER));

            // Execute all queries as one transaction.
            // It is faster, and old data will be kept if some exception is thrown.
            db.beginTransaction();
            db.delete(DbHelper.TABLE_NAME, null, null);

            reader.beginArray();
            while (reader.hasNext() && !isLoadInBackgroundCanceled()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch (name) {
                        case "id":
                            statement.bindLong(1, reader.nextLong());
                            break;
                        case "name":
                            statement.bindString(2, reader.nextString());
                            break;
                        case "genres":
                            List<String> genres = new ArrayList<>();
                            reader.beginArray();
                            while (reader.hasNext()) {
                                genres.add(reader.nextString());
                            }
                            reader.endArray();
                            statement.bindString(3, TextUtils.join(DbHelper.DELIMITER, genres));
                            break;
                        case "tracks":
                            statement.bindLong(4, reader.nextLong());
                            break;
                        case "albums":
                            statement.bindLong(5, reader.nextLong());
                            break;
                        case "link":
                            statement.bindString(6, reader.nextString());
                            break;
                        case "description":
                            statement.bindString(7, reader.nextString());
                            break;
                        case "cover":
                            reader.beginObject();
                            for (int i = 0; i < 2; ++i) {
                                String size = reader.nextName();
                                String cover = reader.nextString();
                                switch (size) {
                                    case "small":
                                        statement.bindString(8, cover);
                                        break;
                                    case "big":
                                        statement.bindString(9, cover);
                                        break;
                                    default:
                                        Log.d(TAG, "Unknown cover size " + size);
                                        break;
                                }
                            }
                            reader.endObject();
                            break;
                        default:
                            Log.w(TAG, "Unknown name '" + name + "'");
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();

                long rowId = statement.executeInsert();
                if (rowId < 0) {
                    return false;
                }
            }

            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            reader.endArray();

            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return true;
    }

}
