package com.artemohanjanyan.mobileschool.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * This AsyncTask is used to delete cover after sharing it.
 */
public class ShareAsyncDeleter extends AsyncTask<String, Void, Void> {

    private static final String TAG = ShareAsyncDeleter.class.getSimpleName();

    private Context context;

    public ShareAsyncDeleter(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String string = params[0];
        String previousPath = getPathFromURI(Uri.parse(string));
        if (previousPath != null && !new File(previousPath).delete()) {
            Log.d(TAG, "file not deleted");
        }
        return null;
    }

    private String getPathFromURI(Uri uri) {
        try (Cursor cursor = context.getContentResolver()
                    .query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
            context = null;
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        }
    }
}
