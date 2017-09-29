package com.udayaproject.pat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import static com.udayaproject.pat.CommonUtilities.PENDING_URL;
import static com.udayaproject.pat.CommonUtilities.TAG;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView t_pname, t_name, t_phone, t_email, t_college, t_aggregate, t_backlogs, t_yop, t_address, t_update, t_branch;
    EditText et_name, et_phone, et_email, et_college, et_aggregate, et_backlogs, et_yop, et_address;
    Button save_details;
    ImageView edit_button;
    Context mContext;
    private String email = "";
    private Spinner spinner;
    //private DataAdapter dataAdapter;
    private String college = "";
    List<String> colleges = new ArrayList<String>();
    List<String> branches = new ArrayList<String>();
    private AsyncTask<Void, Void, Void> mPendingRequest;
    private BranchDataAdapter branchDataAdapter;
    String phone, name, yop, aggregate, branch, backlog, type, college_position;
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
    private AsyncTask<Void, Void, Void> mGetLoggedInUserData;
    private LoggedUser logged_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = ProfileActivity.this;
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.logged_user), Context.MODE_PRIVATE);
        if (sharedPreferences.getString("logged_user", null) != null) {
            String logged_user = sharedPreferences.getString("logged_user", null).trim();
            String[] user = logged_user.split(":");
            email = user[0];
            college_position = user[1];
            type = user[2];
        }

        spinner = (Spinner) findViewById(R.id.spinner_branch);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();

                branch = String.valueOf(position + 1);
                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + branch, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) spinner.getSelectedView()).setError("select college");
            }
        });


        branches.add("CSE");
        branches.add("IT");
        branches.add("ECE");
        branches.add("EEE");
        branches.add("MECH");
        branches.add("CIVIL");
        branches.add("AERO");

        t_name = (TextView) findViewById(R.id.name);
        t_pname = (TextView) findViewById(R.id.listHeadText);
        t_phone = (TextView) findViewById(R.id.phone);
        t_email = (TextView) findViewById(R.id.email);
        t_college = (TextView) findViewById(R.id.college);
        t_backlogs = (TextView) findViewById(R.id.backlogs);
        t_yop = (TextView) findViewById(R.id.yop);
        t_aggregate = (TextView) findViewById(R.id.aggregate);
        t_branch = (TextView) findViewById(R.id.branch);
        t_update = (TextView) findViewById(R.id.update);
        t_update.setVisibility(View.GONE);


        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_college = (EditText) findViewById(R.id.et_college);
        et_backlogs = (EditText) findViewById(R.id.et_backlogs);
        et_yop = (EditText) findViewById(R.id.et_yop);
        et_aggregate = (EditText) findViewById(R.id.et_aggregate);

        save_details = (Button) findViewById(R.id.save_details);
        edit_button = (ImageView) findViewById(R.id.editIcon);

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCollegesData();
                t_name.setVisibility(View.GONE);
                //t_college.setVisibility(View.GONE);
                t_phone.setVisibility(View.GONE);
                //t_email.setVisibility(View.GONE);
                t_aggregate.setVisibility(View.GONE);
                t_yop.setVisibility(View.GONE);
                t_backlogs.setVisibility(View.GONE);
                t_branch.setVisibility(View.GONE);

                et_name.setVisibility(View.VISIBLE);
                //et_college.setVisibility(View.VISIBLE);
                //et_email.setVisibility(View.VISIBLE);
                et_phone.setVisibility(View.VISIBLE);
                et_aggregate.setVisibility(View.VISIBLE);
                et_yop.setVisibility(View.VISIBLE);
                et_backlogs.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                save_details.setVisibility(View.VISIBLE);

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.college), Context.MODE_PRIVATE);
                String college_position = sharedPreferences.getString(email, null).trim();

                colleges = SaveDrives.getCollegesData(mContext);
                String college = colleges.get(Integer.parseInt(college_position) - 1);
                Log.d("COLLEGE", college);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.contact), Context.MODE_PRIVATE);
                String phone = sharedPreferences.getString(email, null).trim();
                Log.d("PHONNEEE", phone);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.branch), Context.MODE_PRIVATE);
                String branch = sharedPreferences.getString(email, null).trim();
                Log.d("BRANCH", branch);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.name), Context.MODE_PRIVATE);
                String name = sharedPreferences.getString(email, null).trim();
                Log.d("NAME", name);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.aggregate), Context.MODE_PRIVATE);
                String aggregate = sharedPreferences.getString(email, null).trim();
                Log.d("AGGREGATE", aggregate);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.yop), Context.MODE_PRIVATE);
                String yop = sharedPreferences.getString(email, null).trim();
                Log.d("YOP", yop);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.backlogs), Context.MODE_PRIVATE);
                String backlog = sharedPreferences.getString(email, null).trim();
                Log.d("BACKLOGS", backlog);

                String setbranch;
                if (!branch.equals("update_required")) {
                    //setbranch = branches.get(Integer.parseInt(branch) - 1);
                    setbranch = "null";
                } else {
                    setbranch = branch;
                }
                et_name.setText(name);
                et_phone.setText(phone);
                et_yop.setText(yop);
                et_backlogs.setText(backlog);
                et_aggregate.setText(aggregate);
            }
        });

        save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = String.valueOf(et_phone.getText());
                name = String.valueOf(et_name.getText());
                backlog = String.valueOf(et_backlogs.getText());
                yop = String.valueOf(et_yop.getText());
                aggregate = String.valueOf(et_aggregate.getText());
                Log.d("CHECK_UPDATE", phone + name + backlog + yop + aggregate);
                showCircle(v, pmessage);

                mPendingRequest = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        Log.d("RECEIVED_POST", "BEFORE_REGISTER");
                        Log.d("RECEIVED_POST", name + ":" + email + ":" + aggregate + ":");

                        sendRequest(mContext, email, college_position, type, phone, name, backlog, yop, aggregate, branch);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {

                        mPendingRequest = null;
                    }

                };
                mPendingRequest.execute(null, null, null);

                //sharedPreferences = ProfileActivity.this.getSharedPreferences(getString(R.string.college), Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.update_done), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(email, "NOT_DONE");
                editor.commit();
                Log.d("UPDATE_S", email);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.contact), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, phone);
                editor.commit();
                Log.d("PHONE_S", phone);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.branch), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, branch);
                editor.commit();
                Log.d("BRANCH_S", branch);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.name), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, name);
                editor.commit();
                Log.d("NAME_S", name);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.yop), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, yop);
                editor.commit();
                Log.d("YOP_S", yop);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.aggregate), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, aggregate);
                editor.commit();
                Log.d("AGGREGATE_S", aggregate);

                sharedPreferences = mContext.getSharedPreferences(getString(R.string.backlogs), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(email, backlog);
                editor.commit();
                Log.d("BACKLOGS_S", backlog);

                Log.d("CHECK_UPDATE", email + phone + name + backlog + yop + aggregate);

                t_name.setText(name);
                t_pname.setText(name);
                //t_college.setText(college);
                t_phone.setText(phone);
                t_email.setText(email);
                t_yop.setText(yop);
                t_backlogs.setText(backlog);
                t_aggregate.setText(aggregate);
                t_branch.setText(branch);


                t_update.setVisibility(View.VISIBLE);
                t_update.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                t_update.setTextColor(getResources().getColor(R.color.white));
                t_update.setText("UPDATE PENDING: CONTACT COORDINATOR");
                t_name.setVisibility(View.VISIBLE);
                //t_college.setVisibility(View.VISIBLE);
                //t_email.setVisibility(View.VISIBLE);
                t_phone.setVisibility(View.VISIBLE);
                t_aggregate.setVisibility(View.VISIBLE);
                t_yop.setVisibility(View.VISIBLE);
                t_backlogs.setVisibility(View.VISIBLE);
                t_branch.setVisibility(View.VISIBLE);

                spinner.setVisibility(View.GONE);
                et_name.setVisibility(View.GONE);
                //et_college.setVisibility(View.GONE);
                //et_email.setVisibility(View.GONE);
                et_phone.setVisibility(View.GONE);
                et_aggregate.setVisibility(View.GONE);
                et_yop.setVisibility(View.GONE);
                et_backlogs.setVisibility(View.GONE);

                save_details.setVisibility(View.GONE);

            }
        });
        setTitle("Profile");
        setDetails();
    }


    private void setCollegesData() {


        // Creating adapter for spinner
        branchDataAdapter = new BranchDataAdapter(this, branches);

        // attaching data adapter to spinner
        spinner.setAdapter(branchDataAdapter);
    }

    private void setUpdate(String name, String phone, String aggregate, String backlog, String yop) {


    }


    private void storeUserData() {
        String done="";
        logged_user = SaveDrives.getLoggedUserData(this);
        if(logged_user.approved.equals("1")){
            done="DONE";
        }
        else {
            done="NOT_DONE";
        }
        Log.d("DONE", done +":"+logged_user.approved);
        SharedPreferences sp = mContext.getSharedPreferences(getString(R.string.update_done), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(email, done);
        e.commit();
    }

    private void setDetails() {

        Log.d("SET_DETAILS", "SET DETAILS");
        storeUserData();

        colleges = SaveDrives.getCollegesData(mContext);
        String college = colleges.get(Integer.parseInt(college_position) - 1);
        Log.d("COLLEGE", college);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.contact), Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(email, null).trim();
        Log.d("PHONNEEE", phone);

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.branch), Context.MODE_PRIVATE);
        String branch = sharedPreferences.getString(email, null).trim();
        Log.d("BRANCH", branch);

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.name), Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(email, null).trim();
        Log.d("NAME", name);

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.aggregate), Context.MODE_PRIVATE);
        String aggregate = sharedPreferences.getString(email, null).trim();
        Log.d("AGGREGATE", aggregate);

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.yop), Context.MODE_PRIVATE);
        String yop = sharedPreferences.getString(email, null).trim();
        Log.d("YOP", yop);

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.backlogs), Context.MODE_PRIVATE);
        String backlog = sharedPreferences.getString(email, null).trim();
        Log.d("BACKLOGS", backlog);

        String setbranch;
        if (!branch.equals("update_required")) {
            Log.d("BRANCH_CHECK", branch);
            setbranch = branches.get(Integer.parseInt(branch) - 1);
            //setbranch ="null";
        } else {
            setbranch = branch;
        }

        sharedPreferences = mContext.getSharedPreferences(getString(R.string.update_done), Context.MODE_PRIVATE);
        String update_done = sharedPreferences.getString(email, null);
        Log.d("UPDATE_DONE", update_done);
        if (update_done != null) {
            t_update.setVisibility(View.VISIBLE);
            if (update_done.equals("DONE")) {
                t_update.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                t_update.setTextColor(getResources().getColor(R.color.white));
                t_update.setText("UPDATED SUCCESSFULLY");
            } else {
                t_update.setVisibility(View.VISIBLE);
            }
        }


        Log.d("CHECK_UPDATE", phone + name + backlog + yop + aggregate);

        t_pname.setText(name);
        t_name.setText(name);
        t_branch.setText(setbranch);
        t_college.setText(college);
        t_phone.setText(phone);
        t_email.setText(email);
        t_yop.setText(yop);
        t_aggregate.setText(aggregate);
        t_backlogs.setText(backlog);
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
    private void sendRequest(Context mContext, String email, String college, String type, String phone, String name, String backlog, String yop, String aggregate, String branch) {

        Log.i(TAG, "sending to (college = " + college + ")");
        String serverUrl = PENDING_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("name", name);
        params.put("phone", phone);
        params.put("backlogs", backlog);
        params.put("yop", yop);
        params.put("percentage", aggregate);
        params.put("branch", branch);
        params.put("college", college);
        params.put("type", type);

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
