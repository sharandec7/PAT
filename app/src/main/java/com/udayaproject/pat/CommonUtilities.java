package com.udayaproject.pat;

/**
 * Created by DETECTIVE7 on 21-02-2016.
 */

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    // give your server registration url here
    static final String MAIL_URL = "http://sharandec7.site40.net/mail.php";
    static final String SERVER_URL = "http://sharandec7.site40.net/register.php";
    static final String DRIVE_URL = "http://sharandec7.site40.net/get_drives.php";
    static final String USER_URL = "http://sharandec7.site40.net/get_user_data.php";
    static final String COMPANIES_URL = "http://sharandec7.site40.net/get_companies.php";
    static final String GET_PENDING_URL = "http://sharandec7.site40.net/get_pending.php";
    static final String COLLEGES_URL = "http://sharandec7.site40.net/get_colleges.php";
    static final String QUESTIONS_URL = "http://sharandec7.site40.net/get_questions.php";
    static final String PUSH_URL = "http://sharandec7.site40.net/send_message.php";
    static final String PENDING_URL = "http://sharandec7.site40.net/pending_data.php";
    static final String DELETE_PENDING_URL = "http://sharandec7.site40.net/delete_pending.php";
    static final String ADD_QUESTION_URL = "http://sharandec7.site40.net/add_question.php";

    // Google project id
    static final String SENDER_ID = "816011506166";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "My_GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.udayaproject.pat.DISPLAY_MESSAGE";

    static final String TOAST_MESSAGE_ACTION =
            "com.udayaproject.pat.TOAST_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
    static final String TOAST_MESSAGE = "toast";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra("type", DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        //Toast.makeText(context, "New Message: " + message, Toast.LENGTH_LONG).show();
        context.sendBroadcast(intent);
    }

    static void displayToast(Context context, String message) {
        Intent intent = new Intent(TOAST_MESSAGE_ACTION);
        intent.putExtra("type", TOAST_MESSAGE_ACTION);
        intent.putExtra(TOAST_MESSAGE, message);
        //Toast.makeText(context, "New Message: " + message, Toast.LENGTH_LONG).show();
        context.sendBroadcast(intent);
    }
}