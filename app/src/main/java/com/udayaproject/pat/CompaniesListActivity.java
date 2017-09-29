package com.udayaproject.pat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CompaniesListActivity extends AppCompatActivity implements CompanyInfoAdapter.MyRecyclerClickListener {

    Toolbar toolbar;
    private AsyncTask<Void, Void, Void> mGetCompanies;
    private Context mcontext;
    private List<CompanyItem> companies_list;
    RecyclerView myRecycler;
    private CompanyInfoAdapter myCompanyInfoAdapter;
    private TextView error_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Companies List");
        mcontext = CompaniesListActivity.this;

        error_message = (TextView) findViewById(R.id.error_message);
        error_message.setText("Please wait as we load the companies list.");

        myRecycler = (RecyclerView) findViewById(R.id.companies_list);
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
                setData();
                mGetCompanies = null;
            }

        };
        mGetCompanies.execute(null, null, null);
    }


    private List<CompanyItem> getData() {
        companies_list = SaveDrives.getCompaniesData(CompaniesListActivity.this);
        return companies_list;
    }

    private void setData() {
        companies_list = getData();
        if (companies_list != null) {
            myRecycler.setVisibility(View.VISIBLE);
            error_message.setVisibility(View.GONE);

            myCompanyInfoAdapter = new CompanyInfoAdapter(this, companies_list);
            myCompanyInfoAdapter.setRecyclerItemClickListener(this);
            myRecycler.setAdapter(myCompanyInfoAdapter);
            myRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        else{
            myRecycler.setVisibility(View.GONE);
            error_message.setVisibility(View.VISIBLE);
            error_message.setText("OOPS! Unable to retrieve company details due to \\n INTERNET Connection Failure\\n  or Server Breakdown. \\n Try again after checking your Internet Connection.");
        }
    }

    @Override
    public void recyclerItemClicked(View view, int position) {
        Intent intent = new Intent(this, CompanyPageActivity.class);
        int pass = position + 1;
        String company_id = "" + pass + ":" + companies_list.get(position).company_name;
        intent.putExtra("company_id", company_id);
        startActivity(intent);
        Toast.makeText(this, "You Clicked item " + position, Toast.LENGTH_LONG).show();
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
