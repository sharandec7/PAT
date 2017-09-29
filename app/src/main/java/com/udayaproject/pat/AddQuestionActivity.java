package com.udayaproject.pat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.udayaproject.pat.CommonUtilities.ADD_QUESTION_URL;
import static com.udayaproject.pat.CommonUtilities.TAG;

public class AddQuestionActivity extends AppCompatActivity {

    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;

    // UI elements
    EditText etQuestion, etO1, etO2, etO3, etO4, etanswer;

    NotificationManager manager;
    Button btnSend;

    String question, option1, option2, option3, option4, answer;
    String message = "";
    private String pmessage = "Attempt";
    private int attempt = 0;

    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 0;

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Post Questions");
        final Context context = this;

        btnSend = (Button) findViewById(R.id.btnPush);

        etQuestion = (EditText) findViewById(R.id.question);
        etO1 = (EditText) findViewById(R.id.option1);
        etO2 = (EditText) findViewById(R.id.option2);
        etO3 = (EditText) findViewById(R.id.option3);
        etO4 = (EditText) findViewById(R.id.option4);
        etanswer = (EditText) findViewById(R.id.answer);

        //tvTrying = (TextView) findViewById(R.id.txtTrying);
        //registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question = etQuestion.getText().toString().trim();
                option1 = etO1.getText().toString().trim();
                option2 = etO2.getText().toString().trim();
                option3 = etO3.getText().toString().trim();
                option4 = etO4.getText().toString().trim();
                answer = etanswer.getText().toString().trim();


                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(AddQuestionActivity.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    // stop executing code by return
                    return;
                }
                showCircle(v, pmessage);
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        sendQuestion(context, question, option1, option2, option3, option4, answer);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "You hit " + item.getTitle(), Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public int downloadFile() {
        while (fileSize <= 100) {
            fileSize++;

            if (fileSize == 10) {
                return 10;
            } else if (fileSize == 20) {
                return 20;
            } else if (fileSize == 30) {
                return 30;
            } else if (fileSize == 40) {
                return 40;
            } else if (fileSize == 50) {
                return 50;
            } else if (fileSize == 70) {
                return 70;
            } else if (fileSize == 80) {
                return 80;
            }
        }
        return 100;
    }

    private void showCircle(View v, final String pmessage) {

        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage("Sending Notifications...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;


        fileSize = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 120) {
                    Log.v("attempt", attempt + "");
                    progressBarStatus = getValue() * 20;
                    Log.v("PROGRESS", progressBarStatus + "");
                    if (progressBarStatus == 100) {
                        attempt = 6;
                    }

                    progressBarbHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setMessage(pmessage + " " + attempt + "...");
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                if (progressBarStatus >= 100) {

                    Log.v("ATTEMPT_MESSAGE_dis", attempt + " " + pmessage);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                }
            }
        }).start();
    }

    /**
     * Register this account/device pair within the server.
     */
    void sendQuestion(final Context context, String question, String opt1, String opt2, String opt3, String opt4, String ans) {


        Log.i(TAG, "sending to (question = " + question + ")");
        String serverUrl = ADD_QUESTION_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("question", question);
        params.put("option1", opt1);
        params.put("option2", opt2);
        params.put("option3", opt3);
        params.put("option4", opt4);
        params.put("answer", ans);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                storeValue(i);
                //displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));\

                post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                //String message = context.getString(R.string.server_registered);
                //CommonUtilities.displayMessage(context, message);

                Log.v("ATTEMPT_MESSAGE", attempt + " " + pmessage);
                attempt = 5;
                pmessage = "Successfull";
                Log.v("ATTEMPT_MESSAGE", attempt + " " + pmessage);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed  to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
    }

    private void storeValue(int i) {
        attempt = i;
    }

    private int getValue() {
        return attempt;
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            // when dialog box is closed, below method will be called.

            PushNotificationActivity.displayDate(year, month, day);
        }
    }
}
