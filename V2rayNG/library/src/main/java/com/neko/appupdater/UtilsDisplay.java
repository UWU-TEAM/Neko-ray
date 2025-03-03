package com.neko.appupdater;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.media.RingtoneManager;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import com.neko.R;
import com.google.android.material.snackbar.Snackbar;

import com.neko.appupdater.enums.UpdateFrom;

import java.net.URL;

class UtilsDisplay {

    static AlertDialog showUpdateAvailableDialog(final Context context, String title, String content, String btnNegative, String btnPositive, String btnNeutral, final DialogInterface.OnClickListener updateClickListener, final DialogInterface.OnClickListener dismissClickListener, final DialogInterface.OnClickListener disableClickListener, Boolean useWebview) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(btnPositive, updateClickListener)
                .setNegativeButton(btnNegative, dismissClickListener)
                .setNeutralButton(btnNeutral, disableClickListener);

        if(useWebview) {
            WebView webView = new WebView(context);
            webView.loadUrl(content);
            alertDialog.setView(webView);
        }else{
            alertDialog.setMessage(content);
        }

        return alertDialog.create();
    }

    static AlertDialog showUpdateNotAvailableDialog(final Context context, String title, String content) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .create();
    }

    static Snackbar showUpdateAvailableSnackbar(final Context context, String content, Boolean indefinite, final UpdateFrom updateFrom, final URL apk) {
        Activity activity = (Activity) context;
        int snackbarTime = indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG;

        /*if (indefinite) {
            snackbarTime = Snackbar.LENGTH_INDEFINITE;
        } else {
            snackbarTime = Snackbar.LENGTH_LONG;
        }*/

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), content, snackbarTime);
        snackbar.setAction(context.getResources().getString(R.string.appupdater_btn_update), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsLibrary.goToUpdate(context, updateFrom, apk);
            }
        });
        return snackbar;
    }

    static Snackbar showUpdateNotAvailableSnackbar(final Context context, String content, Boolean indefinite) {
        Activity activity = (Activity) context;
        int snackbarTime = indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG;

        /*if (indefinite) {
            snackbarTime = Snackbar.LENGTH_INDEFINITE;
        } else {
            snackbarTime = Snackbar.LENGTH_LONG;
        }*/


        return Snackbar.make(activity.findViewById(android.R.id.content), content, snackbarTime);
    }

    static void showUpdateAvailableNotification(Context context, String title, String content, UpdateFrom updateFrom, URL apk, int smallIconResourceId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationChannel(context, notificationManager);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent pendingIntentUpdate = PendingIntent.getActivity(context, 0, UtilsLibrary.intentToUpdate(context, updateFrom, apk), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .addAction(R.drawable.uwu_icon_update, context.getResources().getString(R.string.appupdater_btn_update), pendingIntentUpdate);

            notificationManager.notify(0, builder.build());
        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentUpdate = PendingIntent.getActivity(context, 0, UtilsLibrary.intentToUpdate(context, updateFrom, apk), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .addAction(R.drawable.uwu_icon_update, context.getResources().getString(R.string.appupdater_btn_update), pendingIntentUpdate);

            notificationManager.notify(0, builder.build());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            PendingIntent pendingIntentUpdate = PendingIntent.getActivity(context, 0, UtilsLibrary.intentToUpdate(context, updateFrom, apk), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .addAction(R.drawable.uwu_icon_update, context.getResources().getString(R.string.appupdater_btn_update), pendingIntentUpdate);

            notificationManager.notify(0, builder.build());
        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentUpdate = PendingIntent.getActivity(context, 0, UtilsLibrary.intentToUpdate(context, updateFrom, apk), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .addAction(R.drawable.uwu_icon_update, context.getResources().getString(R.string.appupdater_btn_update), pendingIntentUpdate);

            notificationManager.notify(0, builder.build());
        }
    }

    static void showUpdateNotAvailableNotification(Context context, String title, String content, int smallIconResourceId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationChannel(context, notificationManager);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .setAutoCancel(true);

            notificationManager.notify(0, builder.build());
        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .setAutoCancel(true);

            notificationManager.notify(0, builder.build());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .setAutoCancel(true);

            notificationManager.notify(0, builder.build());
        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, context.getPackageManager().getLaunchIntentForPackage(UtilsLibrary.getAppPackageName(context)), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title, content, smallIconResourceId)
                .setAutoCancel(true);

            notificationManager.notify(0, builder.build());
        }
    }

    private static NotificationCompat.Builder getBaseNotification(Context context, PendingIntent contentIntent, String title, String content, int smallIconResourceId) {
        return new NotificationCompat.Builder(context, context.getString(R.string.appupdater_channel))
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(smallIconResourceId)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

    }

    private static void initNotificationChannel(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    context.getString(R.string.appupdater_channel),
                    context.getString(R.string.appupdater_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

}
