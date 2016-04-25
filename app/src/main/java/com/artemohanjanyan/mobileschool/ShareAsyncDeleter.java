package com.artemohanjanyan.mobileschool;

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

    @Override
    protected Void doInBackground(String... params) {
        String string = params[0];
        String previousPath = getPathFromURI(
                ApplicationContext.getInstance().getApplicationContext(),
                Uri.parse(string));
        if (previousPath != null && !new File(previousPath).delete()) {
            Log.d(TAG, "file not deleted");
        }
        return null;
    }

    private static String getPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor == null) {
                return null;
            }
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
