package com.udayaproject.pat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private AsyncTask<Void, Void, Void> mGetQuestions;
    private Context mContext;
    Button start_test;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Start Test");
        score = (TextView) findViewById(R.id.score);
        start_test = (Button) findViewById(R.id.start_test);
        mContext = QuestionsActivity.this;

        Intent score_i = getIntent();
        String score_action = score_i.getAction();
        if (score_action != null) {
            if (score_action.equals(TestActivity.SCORE)) {
                score.setText("Score: " + score_i.getStringExtra("score"));
                start_test.setText("Take test again");
            }
        }

        start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = QuestionsActivity.this;
                mGetQuestions = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        QuestionsData.getJSON(context);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mGetQuestions = null;
                        Intent i = new Intent(mContext, TestActivity.class);
                        startActivity(i);
                    }

                };
                mGetQuestions.execute(null, null, null);
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
}
