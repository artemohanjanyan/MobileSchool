package com.artemohanjanyan.mobileschool;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;


public class ShareLoader extends AsyncTaskLoader<String> {

    private static final String TAG = ShareLoader.class.getSimpleName();

    public static final String SHARE_ARTIST_EXTRA = "artist";

    private volatile Artist artist;
    private String string;

    public ShareLoader(Context context, Bundle args) {
        super(context);
        artist = args.getParcelable(SHARE_ARTIST_EXTRA);
    }

    @Override
    public String loadInBackground() {
        Log.d(TAG, "cover load started");
        try {
            Bitmap bitmap = ApplicationContext.getInstance().getPicasso().load(artist.bigCover).get();
            return MediaStore.Images.Media.insertImage(
                    ApplicationContext.getInstance().getContentResolver(),
                    bitmap, artist.name, null);
        } catch (IOException ignored) {
            // Sad :(
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (string != null) {
            deliverResult(string);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(String data) {
        string = data;
        super.deliverResult(data);
    }

    @Override
    public void onCanceled(String data) {
        string = data; // Because, we need to delete it.
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        // Delete cover
        if (string != null) {
            new ShareAsyncDeleter().execute(string);
        }
        super.onReset();
    }
}
