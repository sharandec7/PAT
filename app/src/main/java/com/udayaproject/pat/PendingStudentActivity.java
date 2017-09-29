package com.udayaproject.pat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.udayaproject.pat.CommonUtilities.DELETE_PENDING_URL;
import static com.udayaproject.pat.CommonUtilities.PENDING_URL;
import static com.udayaproject.pat.CommonUtilities.TAG;

public class PendingStudentActivity extends AppCompatActivity {

    List<StudentItem> td = new ArrayList<>();
    StudentItem company_page_item = new StudentItem();
    private Context mContext;
    int company_id;
    private AsyncTask<Void, Void, Void> mGetCompanies;
    TextView name, contact, email, branch, backlogs, yop, college, aggregate;
    Button approve;
    private AsyncTask<Void, Void, Void> mPendingRequest;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = PendingStudentActivity.this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Student Info");

        approve = (Button) findViewById(R.id.approve);
        name = (TextView) findViewById(R.id.name);
        contact = (TextView) findViewById(R.id.contact);
        email = (TextView) findViewById(R.id.email);
        branch = (TextView) findViewById(R.id.branch);
        backlogs = (TextView) findViewById(R.id.backlogs);
        yop = (TextView) findViewById(R.id.yop);
        aggregate = (TextView) findViewById(R.id.aggregate);
        college = (TextView) findViewById(R.id.college);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        company_id = getIntent().getIntExtra("student_id", -1);

        final Context context = mContext;
        getData(company_id - 1);
        setData();

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCircle(v, pmessage);
                approveStudent(company_page_item.email);
            }
        });
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


    private void approveStudent(final String email) {

        mPendingRequest = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user
                Log.d("RECEIVED_POST", "BEFORE_REGISTER");
                Log.d("RECEIVED_POST", email );

                sendRequest(mContext, email);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                goBack();
                mPendingRequest = null;
            }

        };
        mPendingRequest.execute(null, null, null);
    }

    private void goBack() {
        Intent i = new Intent(this, PendingListActivity.class);
        startActivity(i);
    }

    private void sendRequest(Context mContext, String email) {
        Log.i(TAG, "sending to (email = " + email + ")");
        String serverUrl = DELETE_PENDING_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);

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
                pmessage = "Successful";
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


    private void getData(int position) {
        td = SaveDrives.getPendingData(PendingStudentActivity.this);
        company_page_item = td.get(position);
    }

    private void setData() {

        Log.d("STUDENT_ID", company_id+"");

        name.setText(company_page_item.name);
        contact.setText(company_page_item.phone);
        email.setText(company_page_item.email);
        branch.setText(company_page_item.branch);
        backlogs.setText(company_page_item.backlogs);
        yop.setText(company_page_item.yop);
        aggregate.setText(company_page_item.percentage);
        college.setText(company_page_item.college);

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

}

