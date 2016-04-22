package com.artemohanjanyan.mobileschool;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadInfoTask extends AsyncTask<URL, Void, List<Artist>> {
    private static final String TAG = "DownloadInfoTask";
    private ListActivity activity;

    public DownloadInfoTask(ListActivity activity) {
        attachActivity(activity);
    }

    public void attachActivity(ListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Artist> doInBackground(URL... params) {
        URL url = params[0];

        try (JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            List<Artist> artists = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext()) {
                Artist artist = new Artist();
                reader.beginObject();

                while (reader.hasNext()) {
                    String name = reader.nextName();
                    try {
                        switch (name) {
                            case "id":
                                artist.id = reader.nextInt();
                                break;
                            case "name":
                                artist.name = reader.nextString();
                                break;
                            case "genres":
                                artist.genres = new ArrayList<>();
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    artist.genres.add(reader.nextString());
                                }
                                reader.endArray();
                                break;
                            case "tracks":
                                artist.tracks = reader.nextInt();
                                break;
                            case "albums":
                                artist.albums = reader.nextInt();
                                break;
                            case "link":
                                artist.link = reader.nextString();
                                break;
                            case "description":
                                artist.description = reader.nextString();
                                break;
                            case "cover":
                                reader.beginObject();
                                for (int i = 0; i < 2; ++i) {
                                    String size = reader.nextName();
                                    String cover = reader.nextString();
                                    switch (size) {
                                        case "small":
                                            artist.smallCover = cover;
                                            break;
                                        case "big":
                                            artist.bigCover = cover;
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
                    } catch (MalformedURLException e) {
                        Log.d(TAG, "Malformed URL", e);
                    }
                }

                reader.endObject();
                artists.add(artist);
            }
            reader.endArray();

            return artists;
        } catch (IOException e) {
            Log.d(TAG, "unknown error", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        if (activity != null) {
            activity.onJsonDownloaded(artists);
        }
    }
}
