package com.udayaproject.pat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestReports extends AppCompatActivity implements TestInfoAdapter.MyRecyclerClickListener {

    Toolbar toolbar;
    String college;
    String[] scores;
    String[] times;
    TextView error_message;
    Context mContext;
    RecyclerView myRecycler;
    public List<TestItem> test_items = new ArrayList<>();
    private String email, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reports);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        error_message = (TextView) findViewById(R.id.error_message);
        mContext = TestReports.this;

        setTitle("Test Reports");
        myRecycler = (RecyclerView) findViewById(R.id.tests_list);

        SharedPreferences sp = this.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
        if (sp.getString("logged_user", null) != null) {
            Log.d("CHECK_COLLEGE", sp.getString("logged_user", null));
            String l_user = sp.getString("logged_user", null).trim();
            String[] user = l_user.split(":");
            email = user[0];
            college = user[1];
            type= user[2];
        }

        sp = this.getSharedPreferences(getString(R.string.test_reports), MODE_PRIVATE);
        if (!sp.getString(email+"TEST_SCORES", "").equals("")) {
            String coded_scores = sp.getString(email+"TEST_SCORES", "").trim();
            String coded_times = sp.getString(email+"TEST_TIMES", "").trim();
            scores = coded_scores.split(":");
            times = coded_times.split(":");
            setData();
        } else {
            setNothingData();
        }
    }

    private void setNothingData() {
        error_message.setVisibility(View.VISIBLE);
        error_message.setText("You haven't taken a Test till now, Please take a test and check reports again.");
        myRecycler.setVisibility(View.GONE);
    }

    private void setData() {
        error_message.setVisibility(View.GONE);
        TestInfoAdapter myInfoAdapter = new TestInfoAdapter(this, getData());
        myInfoAdapter.setRecyclerItemClickListener(this);
        myRecycler.setAdapter(myInfoAdapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<TestItem> getData() {

        for (int i = 0; i < scores.length && i < times.length; i++) {
            if (!scores[i].equals("") && !times[i].equals("") && !scores[i].equals(null) && !times[i].equals(null) && !scores[i].isEmpty() && !times[i].isEmpty()) {

                TestItem current = new TestItem();
                current.score = scores[i];
                current.time = times[i];
                String[] check = scores[i].split("/");
                String message = "";
                double percent = Double.parseDouble(check[0]) / Double.parseDouble(check[1]);
                if (percent >= 0 && percent < 0.3) {
                    message = "Bad Performance";
                } else if (percent > 0.3 && percent < 0.4) {
                    message = "Need to Improve";
                } else if (percent > 0.4 && percent < 0.5) {
                    message = "Average";
                } else if (percent >= 0.5 && percent < 0.6) {
                    message = "Above Average";
                } else if (percent > 0.6 && percent < 0.7) {
                    message = "Good";
                } else if (percent > 0.7 && percent < 0.8) {
                    message = "Great";
                } else if (percent > 0.8 && percent < 0.9) {
                    message = "Excellent";
                } else if (percent > 0.9 && percent <= 1) {
                    message = "Awesome";
                }
                current.message = message;
                test_items.add(current);
            }
        }
        return test_items;
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


    @Override
    public void recyclerItemClicked(View view, int position) {
        Toast.makeText(this, "You Clicked item " + position, Toast.LENGTH_LONG).show();
    }
}
