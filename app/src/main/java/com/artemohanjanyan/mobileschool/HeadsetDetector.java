package com.artemohanjanyan.mobileschool;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class HeadsetDetector extends BroadcastReceiver {
    private static final int MUSIC_NOTIFICATION_ID = 1;
    private static final int RADIO_NOTIFICATION_ID = 2;
    private static final String MUSIC_PACKAGE = "ru.yandex.music";
    private static final String RADIO_PACKAGE = "ru.yandex.radio";

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("state", -1);

        if (state == 1) {
            showNotification(context, MUSIC_PACKAGE, R.string.preference_music,
                    R.string.open_music, MUSIC_NOTIFICATION_ID);
            showNotification(context, RADIO_PACKAGE, R.string.preference_radio,
                    R.string.open_radio, RADIO_NOTIFICATION_ID);
        } else if (state == 0) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(MUSIC_NOTIFICATION_ID);
            notificationManager.cancel(RADIO_NOTIFICATION_ID);
        }
    }

    private void showNotification(Context context,
                                  String appPackage, @StringRes int preferenceRes,
                                  @StringRes int notificationText, int notificationId) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(preferenceRes), true)) {
            return;
        }

        Intent launchIntent =
                context.getPackageManager().getLaunchIntentForPackage(appPackage);
        if (launchIntent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle(context.getString(R.string.headphones_plugged_in))
                    .setContentText(context.getString(notificationText))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);
            try {
                Drawable icon = context.getPackageManager().getApplicationIcon(appPackage);
                builder.setLargeIcon(((BitmapDrawable) icon).getBitmap());
            } catch (Exception ignored) {
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
