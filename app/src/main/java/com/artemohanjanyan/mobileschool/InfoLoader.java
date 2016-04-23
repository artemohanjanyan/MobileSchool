package com.artemohanjanyan.mobileschool;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.OperationCanceledException;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class InfoLoader extends AsyncTaskLoader<List<Artist>> {

    private static final String TAG = InfoLoader.class.getSimpleName();
    private static final String jsonURL = "http://cache-spb03.cdn.yandex.net/" +
            "download.cdn.yandex.net/mobilization-2016/artists.json";
    private DbHelper dbHelper;
    private List<Artist> artists;

    public InfoLoader(Context context) {
        super(context);
    }

    /**
     * Loads information about artists.
     * @return list of artists on success, null if IO error happens.
     */
    @Override
    public List<Artist> loadInBackground() {
        Log.d(TAG, "load started");

        dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = getAll(db);
        if (cursor.getCount() == 0) {
            try {
                if (!download()) {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
            cursor = getAll(db);
        }

        List<Artist> artists = new ArrayList<>(cursor.getCount());

        if (!cursor.moveToFirst()) {
            return artists;
        }

        do {
            Artist artist = new Artist();
            artist.id           = cursor.getInt(0);
            artist.name         = cursor.getString(1);
            artist.genres       = Arrays.asList(cursor.getString(2)
                                        .split(Pattern.quote(DbHelper.DELIMITER)));
            artist.tracks       = cursor.getInt(3);
            artist.albums       = cursor.getInt(4);
            artist.link         = cursor.getString(5);
            artist.description  = cursor.getString(6);
            artist.smallCover   = cursor.getString(7);
            artist.bigCover     = cursor.getString(8);
            artists.add(artist);
        } while (cursor.moveToNext() && !isLoadInBackgroundCanceled());

        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        return artists;
    }

    @Override
    public void deliverResult(List<Artist> data) {
        artists = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (artists == null) {
            forceLoad();
        } else {
            deliverResult(artists);
        }
        super.onStartLoading();
    }

    /**
     * Downloads JSON from the Internet, parses it and writes parsed data to database.
     * @return true on success, false otherwise.
     * @throws IOException if some IO error happens.
     */
    private boolean download() throws IOException {
        try (
                JsonReader reader = new JsonReader(new InputStreamReader(
                        new URL(jsonURL).openStream(), "UTF-8"));
                SQLiteDatabase db = dbHelper.getWritableDatabase()) {

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

            db.beginTransaction();

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
                            Log.d(TAG, "Unknown name '" + name + "'");
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();

                long rowId = statement.executeInsert();
                //noinspection StatementWithEmptyBody
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

    /**
     * Gets all artists from database.
     * @param db database to query.
     * @return {@link Cursor}, positioned before the first entry.
     */
    private Cursor getAll(SQLiteDatabase db) {
        return db.query(DbHelper.TABLE_NAME, null, null, null, null, null, null);
    }
}
