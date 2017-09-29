package com.udayaproject.pat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager mPager;
    TextView running_time;
    ImageButton next_question, prev_question;
    private TestCountDownTimer countDownTimer;
    private final long startTime = 60000;
    private final long interval = 1000;
    public List<QuestionItem> q_items = new ArrayList<>();
    private static Context mContext;

    static final String SCORE = "com.udayaproject.pat.SCORE";
    static int[] answers;
    private TextView error_message;
    RelativeLayout bottom;
    RelativeLayout time_holder;
    private String email, college, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = TestActivity.this;
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.logged_user), MODE_PRIVATE);
        if (sp.getString("logged_user", null) != null) {
            Log.d("CHECK_COLLEGE", sp.getString("logged_user", null));
            String l_user = sp.getString("logged_user", null).trim();
            String[] user = l_user.split(":");
            email = user[0];
            college = user[1];
            type= user[2];
        }
        setTitle("Test");
        getData();
        error_message = (TextView) findViewById(R.id.error_message);
        bottom = (RelativeLayout) findViewById(R.id.bottomLine);
        time_holder = (RelativeLayout) findViewById(R.id.time_holder);

        prev_question = (ImageButton) findViewById(R.id.prev_question);
        next_question = (ImageButton) findViewById(R.id.next_question);

        prev_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
            }
        });
        next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }
        });

        setData();

        running_time = (TextView) findViewById(R.id.running_time);
        countDownTimer = new TestCountDownTimer(startTime, interval);
        running_time.setText(startTime + "");
        countDownTimer.start();
    }

    private void setData() {

        if (q_items != null) {
            error_message.setVisibility(View.GONE);
            mPager = (ViewPager) findViewById(R.id.test_pager);
            mPager.setAdapter(new MyTestPagerAdapter(getSupportFragmentManager()));
        } else {
            error_message.setVisibility(View.VISIBLE);
            time_holder.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
            mPager.setVisibility(View.GONE);
        }

    }


    void getData() {
        q_items = SaveDrives.getQuestionsData(mContext);
        if (q_items != null) {
            answers = new int[q_items.size()];
        }
    }

    class MyTestPagerAdapter extends FragmentPagerAdapter {


        public MyTestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MyTestFragment myTestFragment = MyTestFragment.getInstance(position, q_items);
            return myTestFragment;
        }

        @Override
        public int getCount() {

            int total_questions = 0;
            if (q_items != null) {
                total_questions = q_items.size();
            }
            return total_questions;
        }

    }

    public static class MyTestFragment extends Fragment {
        private TextView question;
        RadioButton rb1, rb2, rb3, rb4;

        final Context context = mContext;

        static List<QuestionItem> ques_items = new ArrayList<>();

        public static MyTestFragment getInstance(int position, List<QuestionItem> q_item) {
            MyTestFragment myTestFragment = new MyTestFragment();
            ques_items = q_item;
            Bundle args = new Bundle();
            args.putInt("position", position);
            myTestFragment.setArguments(args);
            return myTestFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.test_fragment, container, false);
            question = (TextView) layout.findViewById(R.id.question);
            rb1 = (RadioButton) layout.findViewById(R.id.op1);
            rb2 = (RadioButton) layout.findViewById(R.id.op2);
            rb3 = (RadioButton) layout.findViewById(R.id.op3);
            rb4 = (RadioButton) layout.findViewById(R.id.op4);

            Bundle bundle = getArguments();
            final int question_number = bundle.getInt("position");
            if (bundle != null && question_number <= ques_items.size()) {
                setData(question_number);
            }


            RadioGroup rg = (RadioGroup) layout.findViewById(R.id.radioOptions);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.op1:
                            answers[question_number] = 1;
                            //Toast.makeText(context, question_number + "1", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.op2:
                            answers[question_number] = 2;
                            //Toast.makeText(context, question_number + "2", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.op3:
                            answers[question_number] = 3;
                            //Toast.makeText(context, question_number + "3", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.op4:
                            answers[question_number] = 4;
                            //Toast.makeText(context, question_number + "4", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });

            return layout;
        }

        private void setData(int qno) {

            QuestionItem qi = new QuestionItem();
            qi = ques_items.get(qno);
            question.setText(qi.question);
            rb1.setText(qi.option1);
            rb2.setText(qi.option2);
            rb3.setText(qi.option3);
            rb4.setText(qi.option4);
        }
    }

    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (status != null)
            // Setting alert dialog icon
            //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int score = 0;
                    int total = q_items.size();
                    for (int i = 0; i < q_items.size(); i++) {
                        if (answers[i] == q_items.get(i).answer) {
                            score = score + 1;
                        }
                    }
                    SharedPreferences StoreScore = getSharedPreferences(getString(R.string.test_reports), MODE_PRIVATE);
                    String current = StoreScore.getString(email+"TEST_SCORES", "");
                    String current_time = StoreScore.getString(email+"TEST_TIMES", "");
                    SharedPreferences.Editor editor = StoreScore.edit();
                    String score_now = "";
                    String coded_scores = "";
                    String coded_times = "";
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());

                    if (current.equals("")) {
                        score_now = score + "/" + total;
                        coded_scores = ":" + score_now;
                        coded_times = ":" + formattedDate;
                    } else {
                        score_now = score + "/" + total;
                        coded_scores = ":" + score_now + current;
                        coded_times = ":" + formattedDate + current_time;
                    }
                    editor.putString(email+"TEST_SCORES", coded_scores);
                    editor.putString(email+"TEST_TIMES", coded_times);
                    editor.commit();

                    Intent back = new Intent(TestActivity.this, QuestionsActivity.class);
                    back.setAction(SCORE);
                    Log.d("SCORE", score + "");
                    back.putExtra("score", score + "/" + total + "");
                    startActivity(back);
                }
            });

        // Showing Alert Message
        alertDialog.show();
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
            countDownTimer.cancel();
            showAlertDialog(TestActivity.this,
                    "End Test",
                    "Press OK to view score", false);
            //NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TestCountDownTimer extends CountDownTimer {

        public TestCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            running_time.setText("Time's UP!");
            showAlertDialog(TestActivity.this,
                    "Test time elapsed",
                    "Press OK to view score", false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //text.setText("Time remain:" + millisUntilFinished);
            String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            running_time.setText(time);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showAlertDialog(TestActivity.this,
                "End Test",
                "Press OK to view score", false);
    }
}
