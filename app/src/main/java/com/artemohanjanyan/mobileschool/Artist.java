package com.artemohanjanyan.mobileschool;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Stores data about one artist.
 */
public class Artist implements Parcelable {
    public int id;
    public String name;
    public List<String> genres;
    public int tracks, albums;
    public String link;
    public String description;
    public String smallCover, bigCover;

    /**
     * Create artist, reading info from current row of cursor.
     * @param cursor source cursor, should be set to desirable position.
     */
    public Artist(Cursor cursor) {
        id = cursor.getInt(0);
        name = cursor.getString(1);
        genres = Arrays.asList(cursor.getString(2).split(Pattern.quote(DbHelper.DELIMITER)));
        tracks = cursor.getInt(3);
        albums = cursor.getInt(4);
        link = cursor.getString(5);
        description = cursor.getString(6);
        smallCover = getSmallCover(cursor);
        bigCover = getBigCover(cursor);
    }

    /**
     * Returns genres as a comma-separated string.
     */
    public String getGenres() {
        return TextUtils.join(", ", genres);
    }

    /**
     * Returns a string, which describes how many albums and songs artist has published.<br>
     * Forms of nouns correspond to numerals (e.g. 1 album, 2 albums).
     * @param context context used to get access to {@link Resources}.
     */
    public String getPublished(Context context) {
        Resources resources = context.getResources();
        return context.getString(R.string.published,
                resources.getQuantityString(R.plurals.albums, albums, albums),
                resources.getQuantityString(R.plurals.tracks, tracks, tracks));
    }

    /**
     * Returns link to small cover at current row of cursor,
     */
    public static String getSmallCover(Cursor cursor) {
        return cursor.getString(7);
    }

    /**
     * Returns link to big cover at current row of cursor,
     */
    public static String getBigCover(Cursor cursor) {
        return cursor.getString(8);
    }

    /**
     * Creates an instance from parcel.
     * @param in parcel.
     */
    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        genres = in.createStringArrayList();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        description = in.readString();
        smallCover = in.readString();
        bigCover = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeStringList(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(smallCover);
        dest.writeString(bigCover);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
