package com.udayaproject.pat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.udayaproject.pat.CommonUtilities.SENDER_ID;
import static com.udayaproject.pat.CommonUtilities.SERVER_URL;
import static com.udayaproject.pat.CommonUtilities.TAG;
import static com.udayaproject.pat.CommonUtilities.displayToast;

public class RegisterActivity extends Activity {
    // alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    public boolean ss;
    // Register button
    CheckBox admin_check;
    Button bt_login, bt_signup, bt_sign_in, bt_create;
    EditText et_name, et_password, et_email, et_contact;
    ViewFlipper viewFlipper;
    LinearLayout lt_name;
    RelativeLayout brand;
    static final String GCM_REGISTER = "com.udayaproject.pat.GCMREGISTER";
    private static Context mContext;
    private AsyncTask<Void, Void, Void> mGetCollegesData;
    Spinner spinner;
    List<String> colleges = new ArrayList<String>();
    String college = "";

    DataAdapter dataAdapter;
    private AsyncTask<Void, Void, Void> mRegisterTask;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        admin_check = (CheckBox) findViewById(R.id.admin_check);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        bt_sign_in = (Button) findViewById(R.id.bt_sign_in);
        bt_create = (Button) findViewById(R.id.bt_create);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_contact = (EditText) findViewById(R.id.et_phone);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        lt_name = (LinearLayout) findViewById(R.id.lt_name);
        brand = (RelativeLayout) findViewById(R.id.brand);
        spinner = (Spinner) findViewById(R.id.college_list);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();

                college = String.valueOf(position + 1);
                // Showing selected spinner item
                Toast.makeText(parent.getContext(), "Selected: " + college, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) spinner.getSelectedView()).setError("select college");
            }
        });

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }


        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
        if (sharedPreferences.getString("logged_user", null) != null) {
            String logged_user = sharedPreferences.getString("logged_user", null).trim();
            Intent loggedIn = new Intent(mContext, MainActivity.class);
            loggedIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.d("CHECK_REG_COLLEGE", logged_user);
            loggedIn.putExtra("logged", logged_user);
            startActivity(loggedIn);
        } else {

            mGetCollegesData = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    // Register on our server
                    // On server creates a new user

                    //Log.d("CHECK_COLLEGE", college);
                    Colleges.getJSON(RegisterActivity.this);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    setCollegesData();
                    mGetCollegesData = null;
                }
            };
            mGetCollegesData.execute(null, null, null);
        }


        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sender id / server url is missing
            alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brand.setVisibility(View.GONE);
                lt_name.setVisibility(View.VISIBLE);
                viewFlipper.setDisplayedChild(1);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brand.setVisibility(View.VISIBLE);
                lt_name.setVisibility(View.GONE);
                viewFlipper.setDisplayedChild(0);
            }
        });

        bt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (validateLogin(email, password)) {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.college), MODE_PRIVATE);
                    String college = sharedPreferences.getString(email, null).trim();
                    sharedPreferences = mContext.getSharedPreferences(getString(R.string.type), Context.MODE_PRIVATE);
                    String type = sharedPreferences.getString(email, null).trim();
                    sharedPreferences = mContext.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String clg_type = email + ":" + college + ":" + type;
                    editor.putString("logged_user", clg_type);
                    editor.commit();

                    Log.d("CHECK_COLLEGE_IN", clg_type);

                    Intent welcomeActivity = new Intent(RegisterActivity.this, MainActivity.class);
                    welcomeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    welcomeActivity.putExtra("email", email);
                    startActivity(welcomeActivity);
                } else {
                    alert.showAlertDialog(RegisterActivity.this, "Login Error!", "Please enter valid credentials.", false);
                }
            }
        });

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String name = et_name.getText().toString().trim();
                String phone = et_contact.getText().toString().trim();
                String type;
                if (admin_check.isChecked()) {
                    type = "0";
                } else {
                    type = "1";
                }

                if (validateCreate(email, password, name, phone, college, type)) {

                    showCircle(v, pmessage);
                    addToDb(email, password, name, phone, college, type);

                    //checkUserAlreadyExists(email, password, name, phone, college, type);

                } else {
                    alert.showAlertDialog(RegisterActivity.this, "Registration Error!", "Please enter valid credentials.", false);
                }
            }
        });
    }

    private void checkUserAlreadyExists(String email, String password, String name, String phone, String college, String type) {
        String success_exists;
        SharedPreferences sp = mContext.getSharedPreferences(getResources().getString(R.string.reg_success), MODE_PRIVATE);
        String result = sp.getString("reg_success_json", null);
        try {
            Log.d("RESULT_SUCCESS",result);
            JSONObject response = new JSONObject(result);
            Log.d("RESULT_SUCCESS",response.optString("success2"));
            success_exists = response.optString("success2");
        } catch (JSONException e) {
            e.printStackTrace();
            success_exists = "";
        }
        if (success_exists.equals("true")) {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.cred), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(email, password);
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.name), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, name);
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.branch), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, "update_required");
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.yop), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, "update_required");
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.aggregate), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, "update_required");
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.backlogs), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, "update_required");
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.college), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, college);
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.contact), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, phone);
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.type), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(email, type);
            editor.commit();
            sharedPreferences = mContext.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            String clg_type = email + ":" + college + ":" + type;
            editor.putString("logged_user", clg_type);
            editor.commit();

            Log.d("CHECK_COLLEGE_UP", clg_type);

            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setAction(GCM_REGISTER);
            i.putExtra("name", name);
            i.putExtra("email", email);
            i.putExtra("college", college);
            i.putExtra("phone", phone);
            i.putExtra("type", type);
            startActivity(i);
            finish();
        } else {
            alert.showAlertDialog(RegisterActivity.this, "Registration Error!", "User Already exists, Please Login in as: " + email, false);
        }
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

    private void storeValue(int i) {
        attempt = i;
    }

    private int getValue() {
        return attempt;
    }

    private void addToDb(final String email, final String password, final String name, final String phone, final String college, final String type) {


        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        Log.d("RECEIVED_POST", regId);

        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            Log.d("RECEIVED_POST", "REGID_EMPTY");
            GCMRegistrar.register(this, SENDER_ID);
            Log.v("reg_id", regId);
        } else {
            Log.v("reg_id", regId);
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
                final Context context = RegisterActivity.this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {


                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        Log.d("RECEIVED_POST", "BEFORE_REGISTER");
                        Log.d("RECEIVED_POST", name + ":" + email + ":" + college + ":" + regId);

                        sendToDb(context, name, email, password, college, phone, type, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        checkUserAlreadyExists(email, password, name, phone, college, type);
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = RegisterActivity.this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {


                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        Log.d("RECEIVED_POST", "BEFORE_REGISTER");
                        Log.d("RECEIVED_POST", name + ":" + email + ":" + college + ":" + regId);

                        sendToDb(context, name, email, password, college, phone, type, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        checkUserAlreadyExists(email, password, name, phone, college, type);
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
        String gcmId = GCMRegistrar.getRegistrationId(this);
        Log.d("GCM_ID", regId);
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.regId), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("gcmId", gcmId);
        editor.commit();
    }

    private void setSS() {

    }


    private void sendToDb(Context context, String name, String email, String password, String college, String phone, String type, String regId) {
        mContext = context;
        Log.i(TAG, "registering device (email = " + email + ")");
        Log.d("RECEIVED", name + ":" + email + ":" + college);

        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("college", college);
        params.put("phone", phone);
        params.put("type", type);
        params.put("regId", regId);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayToast(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
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
                GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                CommonUtilities.displayToast(context, message);
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
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        CommonUtilities.displayToast(context, message);
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

        InputStream inputUserStream = null;

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
            if (status == HttpURLConnection.HTTP_OK) {
                inputUserStream = new BufferedInputStream(conn.getInputStream());
                String response = convertUserInputStreamToString(inputUserStream);
                //parseResult(response);
            } else {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String convertUserInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }
        sendUserResult(result);
        return result;
    }

    private static void sendUserResult(String result) {
        Log.d("RESULT_SUCCESS",result);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.reg_success), mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reg_success_json", "");
        editor.commit();
        editor.putString("reg_success_json", result);
        editor.commit();
    }


    private void setCollegesData() {

        colleges = SaveDrives.getCollegesData(RegisterActivity.this);
        // Creating adapter for spinner
        dataAdapter = new DataAdapter(this, colleges);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    private boolean validateCreate(String email, String password, String name, String phone, String college, String type) {

        if (!isValidName(name)) {
            return false;
        }
        if (!isValidEmail(email)) {
            return false;
        }
        if (!isValidPassword(password)) {
            return false;
        }
        if (!isValidPhone(phone)) {
            return false;
        }

        return true;
    }

    private boolean validateLogin(String email, String password) {
        if (!isValidEmail(email)) {
            return false;
        }
        if (!isValidPassword(password)) {
            return false;
        }
        Log.v("email_password", email + "  " + password);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.cred), MODE_PRIVATE);
        if (sharedPreferences.getString(email, null) != null) {
            String s_password = sharedPreferences.getString(email, null).trim();
            Log.v("EMAIL_NULL", sharedPreferences.getString(email, null));
            if (!(password.equals(s_password))) {
                Log.v("EMAIL_NOTMATCH", sharedPreferences.getString(email, null) + " " + password);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isValidName(String name) {
        if (name.isEmpty()) {
            et_name.setError("Name is empty, please enter a valid name with alphanumeric values");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPhone(String phone) {
        if (phone.isEmpty()) {
            et_contact.setError("Contact Number is empty, please enter a valid 10 digit number");
            return false;
        } else {
            String phone_regex = "[0-9]{10}";
            Pattern phone_pattern = Pattern.compile(phone_regex);
            Matcher matcher = phone_pattern.matcher(phone);
            if (phone.length() != 10) {
                et_contact.setError("Contact Number should be of 10 digits");
                return false;
            } else {
                if (!matcher.matches()) {
                    et_contact.setError("Please enter a valid 10 digit number");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }


    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            et_password.setError("Password empty, please enter a password with at least one digit, one uppercase, one lowercase and one special character with minimum 6 characters");
            return false;
        } else {
            /* String password_regex_2 = "[A-Z]";
            String password_regex_3 = "[0-9]";
            String password_regex_4 = "[$&+,;:=?@#|'<>.^*()%!_-]";

            Pattern phone_pattern_1 = Pattern.compile(password_regex_2);
            Pattern phone_pattern_2 = Pattern.compile(password_regex_3);
            Pattern phone_pattern_3 = Pattern.compile(password_regex_4);

            Matcher matcher1 = phone_pattern_1.matcher(password);
            Matcher matcher2 = phone_pattern_2.matcher(password);
            Matcher matcher3 = phone_pattern_3.matcher(password);

            String password_regex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[$&+,;:=?@#|'<>.^*()%!_-]))";
            Pattern phone_pattern = Pattern.compile(password_regex);
            Matcher matcher = phone_pattern.matcher(password);

            if (password.length() < 6) {
                et_password.setError("Password is small, please enter a password with at least one digit, one uppercase, one lowercase and one special character with minimum 6 characters");
                return false;
            } else {
                if (!matcher.matches()){
                 //|| !matcher1.matches() || !matcher2.matches() || !matcher3.matches()) {
                    et_password.setError("Password is invalid, please enter a password with at least one digit, one uppercase, one lowercase and one special character with minimum 6 characters");
                    return false;
                } else {
                    return true;
                }
            } */
            return true;
        }
    }

    public boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            et_email.setError("Email is empty, please enter a valid email address");
            return false;
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return true;
            } else {
                et_email.setError("Enter a valid email address");
                return false;
            }
        }
    }
}
