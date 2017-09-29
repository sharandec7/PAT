package com.udayaproject.pat;

/**
 * Created by DETECTIVE7 on 22-02-2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import static com.udayaproject.pat.CommonUtilities.SENDER_ID;
import static com.udayaproject.pat.CommonUtilities.displayMessage;
import static com.udayaproject.pat.CommonUtilities.displayToast;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        String gcmId = GCMRegistrar.getRegistrationId(this);
        Log.d("GCM_ID", registrationId);
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.regId), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("gcmId", gcmId);
        editor.commit();
        displayToast(context, "Your device registered with GCM");
        Log.d("NAME", "MainActivity.name");
        Log.d("RECEIVED_POST", "BEFORE_REGISTER");
        Log.d("ON_REGISTERED",MainActivity.name+":"+MainActivity.email+":"+MainActivity.college+":"+registrationId);
        ServerUtilities.register(context, MainActivity.name, MainActivity.email, MainActivity.college, MainActivity.phone, MainActivity.type, registrationId);
    }

    /**`
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayToast(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayToast(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayToast(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayToast(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.mipmap.place_me;
        //long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification mNotification;

        String title = context.getString(R.string.app_name);

        /*Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification); */

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //ringtone default
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setAutoCancel(true);
        builder.setTicker("Notification from PLaceMe");
        String[] separated = message.split(":d");
        String[] company = separated[0].split("@");
        builder.setContentTitle(company[1]+" Drive");
        builder.setContentText("Offering: "+separated[1]+" @ "+separated[2]);
        builder.setSmallIcon(icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(false);
        //builder.setSubText("Look into the details");   //API level 16
        //builder.setNumber(100);
        //builder.addAction(android.R.drawable.ic_menu_view, "View details", pendingIntent);
        //builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setPriority(Notification.PRIORITY_HIGH);

        // Vibrate if vibrate is enabled
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        // Play default notification sound
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setSound(uri);
        builder.build();

        mNotification = builder.build();
        notificationManager.notify(11, mNotification);
    }

}
