package com.udayaproject.pat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import java.util.ArrayList;
import java.util.List;

import static com.udayaproject.pat.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.udayaproject.pat.CommonUtilities.SENDER_ID;
import static com.udayaproject.pat.CommonUtilities.TOAST_MESSAGE;
import static com.udayaproject.pat.CommonUtilities.TOAST_MESSAGE_ACTION;

public class MainActivity extends AppCompatActivity implements InfoAdapter.MyRecyclerClickListener {

    public static String name = "";
    public static String college = "";
    public static String email = "";
    public static String phone = "";
    public static String type = "";

    // AsyncTask
    AsyncTask<Void, Void, Void> mRegisterTask;
    AsyncTask<Void, Void, Void> mGetData;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;

    Toolbar toolbar;
    Context mContext;
    RecyclerView myRecycler;
    private InfoAdapter myInfoAdapter;
    TextView logged_in_user, error_message;

    public List<Information> data = new ArrayList<>();
    public List<DriveItem> drive_items = new ArrayList<>();


    DrawerLayout mDrawerLayout;
    private AsyncTask<Void, Void, Void> mGetCollegesData;
    private AsyncTask<Void, Void, Void> mGetLoggedInUserData;
    private LoggedUser logged_user;
    private int type_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mContext = MainActivity.this;

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        //register the broadcast receiver
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        logged_in_user = (TextView) findViewById(R.id.logged_user);
        error_message = (TextView) findViewById(R.id.error_message);


        SharedPreferences sp = this.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
        if (sp.getString("logged_user", null) != null) {
            Log.d("CHECK_COLLEGE", sp.getString("logged_user", null));
            String l_user = sp.getString("logged_user", null).trim();
            String[] user = l_user.split(":");
            email = user[0];
            college = user[1];
            logged_in_user.setText(user[0]);
            type_admin = Integer.parseInt(user[2]);
        }

        Intent register = getIntent();
        String registerIntent = register.getAction();
        if (registerIntent != null) {
            if (registerIntent.equals(RegisterActivity.GCM_REGISTER)) {
                Log.d("RECEIVED_POST", RegisterActivity.GCM_REGISTER);
                name = register.getStringExtra("name");
                email = register.getStringExtra("email");
                college = register.getStringExtra("college");
                phone = register.getStringExtra("phone");
                type = register.getStringExtra("type");
                Log.d("RECEIVED_POST", name + ":" + email + ":" + college);
                //getRegistrationId(name, college, email, phone, type);
            }
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        myRecycler = (RecyclerView) findViewById(R.id.homeRecycler);

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

        mGetData = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user

                Log.d("CHECK_COLLEGE", college);
                Drive.getJSON(mContext, college);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setData();
                mGetData = null;
            }
        };
        mGetData.execute(null, null, null);

        mGetCollegesData = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user

                Log.d("CHECK_COLLEGE", college);
                Colleges.getJSON(mContext);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setData();
                mGetCollegesData = null;
            }
        };
        mGetCollegesData.execute(null, null, null);

    }

    private void setData() {
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.logged_user_data), MODE_PRIVATE);
        if (sp.getString("logged_user_json", null) != null) {
            Log.d("CHECK_USER_DATA", sp.getString("logged_user_json", null));
            storeUserData();
            if (logged_user.approved.equals("1")) {
                drive_items = getData();
                if (drive_items != null) {
                    myRecycler.setVisibility(View.VISIBLE);
                    error_message.setVisibility(View.GONE);
                    myInfoAdapter = new InfoAdapter(this, drive_items);
                    myInfoAdapter.setRecyclerItemClickListener(this);
                    myRecycler.setAdapter(myInfoAdapter);
                    myRecycler.setLayoutManager(new LinearLayoutManager(this));
                } else {
                    myRecycler.setVisibility(View.GONE);
                    error_message.setVisibility(View.VISIBLE);
                }
            } else {
                if (type_admin == 1) {
                    myRecycler.setVisibility(View.GONE);
                    error_message.setText("Your account hasn't been approved by your college admin, Please get your account approved by your college coordinator to view drive details");
                    error_message.setVisibility(View.VISIBLE);
                }
                else if (type_admin == 0)
                {
                    myRecycler.setVisibility(View.GONE);
                    error_message.setText("Your account hasn't been approved by your central admin, Please get your account approved.");
                    error_message.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void storeUserData() {
        logged_user = SaveDrives.getLoggedUserData(this);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }

    public List<DriveItem> getData() {

        drive_items = SaveDrives.getData(mContext);
        return drive_items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(this, "You hit " + item.getTitle(), Toast.LENGTH_LONG).show();
                return true;

            case R.id.share:
                Toast.makeText(this, "You hit " + item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void recyclerItemClicked(View view, int position) {
        Intent intent = new Intent(this, DrivePageActivity.class);
        intent.putExtra("position", position);
        passData(position);
        startActivity(intent);
        Toast.makeText(this, "You Clicked item " + position, Toast.LENGTH_LONG).show();
    }

    void passData(int position) {
        DrivePageActivity d = new DrivePageActivity();
        d.pass(drive_items.get(position));
    }

    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("CHECK_MESSAGE", intent.getStringExtra("type"));
            if (intent.getStringExtra("type").equals(DISPLAY_MESSAGE_ACTION)) {

                Log.d("CHECK_MESSAGE", intent.getStringExtra("type"));
                // Waking up mobile if it is sleeping
                WakeLocker.acquire(getApplicationContext());

                Toast.makeText(getApplicationContext(), "New Message from PlaceMe ", Toast.LENGTH_LONG).show();

                // Releasing wake lock
                WakeLocker.release();
            }
            if (intent.getStringExtra("type").equals(TOAST_MESSAGE_ACTION)) {
                Log.d("CHECK_MESSAGE", intent.getStringExtra("type"));
                String newMessage = intent.getExtras().getString(TOAST_MESSAGE);
                Toast.makeText(getApplicationContext(), newMessage, Toast.LENGTH_LONG).show();
            }

            /**
             * Take appropriate action on this messagex
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
        }
    };

    @Override
    protected void onDestroy() {

        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister_Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
}
