package com.artemohanjanyan.mobileschool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
    private static final String TAG = "DownloadImageTask";
    private DownloadCallback<Bitmap> callback;

    public DownloadImageTask(DownloadCallback<Bitmap> callback) {
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        URL url = params[0];

        try {
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while downloading image", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (callback != null) {
            callback.onDownloaded(bitmap);
        }
    }
}
