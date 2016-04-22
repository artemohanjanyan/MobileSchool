package com.artemohanjanyan.mobileschool;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

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

    public Artist() {
    }

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

    public String getGenres() {
        return TextUtils.join(", ", genres);
    }

    public String getPublished(Context context) {
        Resources resources = context.getResources();
        return context.getString(R.string.published,
                resources.getQuantityString(R.plurals.albums, albums, albums),
                resources.getQuantityString(R.plurals.tracks, tracks, tracks));
    }
}
