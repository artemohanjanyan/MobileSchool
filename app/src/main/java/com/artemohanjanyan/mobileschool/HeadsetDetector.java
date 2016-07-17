package com.artemohanjanyan.mobileschool;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class HeadsetDetector extends BroadcastReceiver {
    private static final int MUSIC_NOTIFICATION_ID = 1;
    private static final String MUSIC_PACKAGE = "ru.yandex.music";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(MUSIC_PACKAGE);
        if (launchIntent != null && intent.getIntExtra("state", -1) == 1) {
            Log.d(HeadsetDetector.class.getSimpleName(), "showing music notification...");
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle(context.getString(R.string.open_music))
                    .setContentIntent(pendingIntent);
            try {
                Drawable icon = context.getPackageManager().getApplicationIcon(MUSIC_PACKAGE);
                builder.setLargeIcon(((BitmapDrawable) icon).getBitmap());
            } catch (Exception ignored) {
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(MUSIC_NOTIFICATION_ID, builder.build());
        }
    }
}
