package com.udayaproject.pat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CompanyPageActivity extends AppCompatActivity {

    List<CompanyItem> td = new ArrayList<>();
    CompanyItem company_page_item = new CompanyItem();
    private Context mcontext;
    String company_id;
    private AsyncTask<Void, Void, Void> mGetCompanies;
    TextView location, contact, email, test_sites, description, last_updated, website, view_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mcontext = CompanyPageActivity.this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Company Title");

        location = (TextView) findViewById(R.id.location);
        contact = (TextView) findViewById(R.id.contact);
        email = (TextView) findViewById(R.id.email);
        website = (TextView) findViewById(R.id.website);
        test_sites = (TextView) findViewById(R.id.tests);
        description = (TextView) findViewById(R.id.description);
        last_updated = (TextView) findViewById(R.id.last_updated);
        view_map = (TextView) findViewById(R.id.view_map);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        String[] separated = getIntent().getStringExtra("company_id").split(":");
        setTitle(separated[1]);
        company_id = separated[0];

        final Context context = mcontext;
        mGetCompanies = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user
                Companies.getJSON(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mGetCompanies = null;
            }

        };
        mGetCompanies.execute(null, null, null);
        getData(Integer.parseInt(company_id) - 1);
        setData();
    }

    private void getData(int position) {
        td = SaveDrives.getCompaniesData(CompanyPageActivity.this);
        company_page_item = td.get(position);
    }

    private void setData() {

        Log.d("COMPANY_ID", company_id);

        location.setText(company_page_item.location);
        contact.setText(company_page_item.contact);
        email.setText(company_page_item.email);
        website.setText(company_page_item.website);
        description.setText(company_page_item.description);
        test_sites.setText(company_page_item.tests);
        last_updated.setText("Last Updated: " + company_page_item.last_updated);

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
