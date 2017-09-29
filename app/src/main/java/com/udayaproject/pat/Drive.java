package com.udayaproject.pat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.udayaproject.pat.CommonUtilities.DRIVE_URL;
import static com.udayaproject.pat.CommonUtilities.USER_URL;
import static com.udayaproject.pat.CommonUtilities.TAG;

/**
 * Created by DETECTIVE7 on 25-03-2016.
 */
public final class Drive {

    Toolbar toolbar;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 0;
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    AsyncTask<Void, Void, Void> mGetData;
    String college;
    private String pmessage = "Attempt";
    private static int attempt = 0;
    TextView report;
    static Context c;

    static void getJSON(final Context context, String college) {
        c = context;
        String serverUrl = DRIVE_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("college", college);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                storeValue(i);
                post(serverUrl, params);
                return;
            } catch (IOException e) {
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

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        InputStream inputStream = null;

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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            // handle the response
            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(conn.getInputStream());
                String response = convertInputStreamToString(inputStream);
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
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
        sendResult(result);
        return result;
    }

    private static void sendResult(String result) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(c.getString(R.string.drives),  c.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("drives_json", "");
        editor.commit();
        editor.putString("drives_json", result);
        editor.commit();
    }

    private static void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("drives");

            //JSONArray success_result  = new JSONArray(result);
            //Log.d("SUCCESS_FAIL",success_result.toString());

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String organization = post.optString("o");
                Log.d("ORGANIZATION", organization);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void storeValue(int i) {
        attempt = i;
    }

    private int getValue() {
        return attempt;
    }

    static void getUserJSON(final Context context, String email) {
        c = context;
        //String serverUrl = DRIVE_URL;
        String userUrl = USER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                //storeValue(i);
                postUser(userUrl, params);
                return;
            } catch (IOException e) {
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

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    private static void postUser(String endpoint, Map<String, String> params)
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

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
        SharedPreferences sharedPreferences = c.getSharedPreferences(c.getString(R.string.logged_user_data),  c.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("logged_user_json", "");
        editor.commit();
        editor.putString("logged_user_json", result);
        editor.commit();
    }
}
