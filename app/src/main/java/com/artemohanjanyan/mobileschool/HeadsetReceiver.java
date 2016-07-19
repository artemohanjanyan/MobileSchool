package com.artemohanjanyan.mobileschool;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class HeadsetReceiver extends BroadcastReceiver {
    private static final int MUSIC_NOTIFICATION_ID = 1;
    private static final int RADIO_NOTIFICATION_ID = 2;
    private static final String MUSIC_PACKAGE = "ru.yandex.music";
    private static final String RADIO_PACKAGE = "ru.yandex.radio";

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("state", -1);

        if (state == 1) {
            showNotification(context, MUSIC_PACKAGE, R.string.preference_music,
                    R.string.open_music, R.string.download_music, MUSIC_NOTIFICATION_ID);
            showNotification(context, RADIO_PACKAGE, R.string.preference_radio,
                    R.string.open_radio, R.string.download_radio, RADIO_NOTIFICATION_ID);
        } else if (state == 0) {
            hideNotifications(context);
        }
    }

    private void showNotification(Context context,
                                  String appPackage, @StringRes int preferenceRes,
                                  @StringRes int notificationText, @StringRes int installText,
                                  int notificationId) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(preferenceRes), true)) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(context.getString(R.string.headphones_plugged_in))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appPackage);
        if (launchIntent != null) {
            builder.setContentText(context.getString(notificationText));
            try {
                Drawable icon = context.getPackageManager().getApplicationIcon(appPackage);
                builder.setLargeIcon(((BitmapDrawable) icon).getBitmap());
            } catch (PackageManager.NameNotFoundException e) {
                // Should not happen.
                // If launchIntent != null then app is installed.
                // And both Yandex.Music and Yandex.Radio have icons.
                // But nevertheless:
                Log.e(HeadsetReceiver.class.getSimpleName(), "error while getting app icon", e);
            }
        } else {
            launchIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackage));
            builder.setContentText(context.getString(installText));
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher));
        }
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    public void hideNotifications(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(MUSIC_NOTIFICATION_ID);
        notificationManager.cancel(RADIO_NOTIFICATION_ID);
    }
}
