package com.udayaproject.pat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DrivePageActivity extends AppCompatActivity {

    DriveItem drive_page_item = new DriveItem();
    String company_id, organization;
    Context context;
    TextView org, view_org, des, salary, criteria, branch, date, venue;
    static final String DRIVE_PAGE_ACTION = "com.udayaproject.pat.DRIVE_PAGE_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = DrivePageActivity.this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Drive Title");

        int position = getIntent().getIntExtra("position", -1);
        org = (TextView) findViewById(R.id.organization);
        view_org = (TextView) findViewById(R.id.view_org);
        des = (TextView) findViewById(R.id.designation);
        salary = (TextView) findViewById(R.id.salary);
        criteria = (TextView) findViewById(R.id.criteria);
        branch = (TextView) findViewById(R.id.branch);
        date = (TextView) findViewById(R.id.date);
        venue = (TextView) findViewById(R.id.venue);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getData(position);
        setData();
        setTitle(organization + " Drive");
        view_org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DrivePageActivity.this, CompanyPageActivity.class);
                i.setAction(DRIVE_PAGE_ACTION);
                i.putExtra("company_id", drive_page_item.oraganization);
                startActivity(i);
            }
        });
    }

    private void getData(int position) {
        List<DriveItem> td = new ArrayList<>();
        td = SaveDrives.getData(DrivePageActivity.this);
        drive_page_item = td.get(position);
        String[] separated = drive_page_item.oraganization.split(":");
        company_id = separated[0];
    }

    private void setData() {
        String[] separated = drive_page_item.oraganization.split(":");
        organization = separated[1];
        org.setText(organization);
        des.setText(drive_page_item.designation);
        salary.setText(drive_page_item.salary);
        criteria.setText(drive_page_item.criteria);
        branch.setText(drive_page_item.branch);
        date.setText(drive_page_item.date);
        venue.setText(drive_page_item.venue);
    }

    void pass(DriveItem drive_page_item) {
        this.drive_page_item = drive_page_item;
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
