package com.udayaproject.pat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SplashScreenActivity extends AppCompatActivity {


    public static String name = "";
    public static String email = "";

    // alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    private final long startTime = 1000;
    private final long interval = 100;
    private TestCountDownTimer countDownTimer;

    AsyncTask<Void, Void, Void> mGetData;
    private Context mContext;
    String college = "";
    private AsyncTask<Void, Void, Void> mGetLoggedInUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(SplashScreenActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        mContext = SplashScreenActivity.this;
        countDownTimer = new TestCountDownTimer(startTime, interval);
        countDownTimer.start();

        SharedPreferences sp = this.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
        if (sp.getString("logged_user", null) != null) {
            Log.d("CHECK_COLLEGE", sp.getString("logged_user", null));
            String l_user = sp.getString("logged_user", null).trim();
            String[] user = l_user.split(":");
            email = user[0];
            college = user[1];
        }
        mGetLoggedInUserData = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user

                Log.d("CHECK_EMAIL", email);
                Drive.getUserJSON(mContext, email);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mGetLoggedInUserData = null;
            }
        };
        mGetLoggedInUserData.execute(null, null, null);
    }


    public class TestCountDownTimer extends CountDownTimer {

        public TestCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            SharedPreferences sp = SplashScreenActivity.this.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);

            if (sp.getString("logged_user", null) != null) {

                Log.d("CHECK_COLLEGE", sp.getString("logged_user", null));
                String l_user = sp.getString("logged_user", null).trim();
                String[] user = l_user.split(":");
                college = user[1];

                String logged_user = sp.getString("logged_user", null).trim();
                Intent loggedIn = new Intent(SplashScreenActivity.this, MainActivity.class);
                loggedIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                loggedIn.putExtra("logged", logged_user);
                startActivity(loggedIn);

            } else {

                Intent notLoggedIn = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                startActivity(notLoggedIn);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
            //      TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
            //    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
        }
    }
}
