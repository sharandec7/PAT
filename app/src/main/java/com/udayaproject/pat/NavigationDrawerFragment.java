package com.udayaproject.pat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DETECTIVE7 on 05-12-2015.
 */
public class NavigationDrawerFragment extends Fragment implements DrawerInfoAdapter.MyDrawerClickListener {


    private static final String DRAWER_ITEM_POSITION = "drawer_item_position";
    private RecyclerView recycler;
    private static final String PREF_FILE_NAME = "textpref";
    private static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DrawerInfoAdapter mydrawerinfoadapter;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstance;
    private View containerView;

    int type;

    List<Information> data = new ArrayList<>();

    String[] titles = {
            "Profile",
            "Tests",
            "Test Reports",
            "Companies",
            "Contact us",
            "Logout"};

    String[] admin_titles = {
            "Profile",
            "Pending Requests",
            "Post Notification",
            "Post questions",
            "Companies",
            "Contact us",
            "Logout"};
    private int icon = R.drawable.ic_action_next_icon;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstance = true;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.logged_user), Context.MODE_PRIVATE);
        String logged_user = sharedPreferences.getString("logged_user", null).trim();
        String[] user = logged_user.split(":");
        type = Integer.parseInt(user[2]);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recycler = (RecyclerView) layout.findViewById(R.id.drawer_list);
        mydrawerinfoadapter = new DrawerInfoAdapter(getActivity(), getData());
        mydrawerinfoadapter.setDrawerItemClickListener(this);
        recycler.setAdapter(mydrawerinfoadapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    public List<Information> getData() {

        String[] title;
        if (type == 1) {
            title = titles;
        } else {
            title = admin_titles;
        }
        for (int i = 0; i < title.length; i++) {
            Information current = new Information();
            current.iconId = icon;
            current.title = title[i];
            data.add(current);
        }
        return data;
    }


    public void setUp(int fragmentID, DrawerLayout drawLayout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(R.id.fragment_navigation_drawer);
        mDrawerLayout = drawLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //Log.d("sai sharan", "sai" + slideOffset);
                //if (slideOffset <= 0.5) {
                //  toolbar.setAlpha(1 - slideOffset);
                //}
            }

        };


        if (!mUserLearnedDrawer && !mFromSavedInstance) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void drawerItemClicked(View view, int position) {
        if (type == 1) {
            if (position == 0) {
                startNew(ProfileActivity.class, position);
            } else if (position == 1) {
                startNew(QuestionsActivity.class, position);
            } else if (position == 2) {
                startNew(TestReports.class, position);
            } else if (position == 3) {
                startNew(CompaniesListActivity.class, position);
            } else if (position == 4) {
                startNew(ContactUs.class, position);
            } else if (position == 5) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.logged_user), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("logged_user", null);
                editor.commit();
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        } else {
            if (position == 0) {
                startNew(ProfileActivity.class, position);
            } else if (position == 1) {
                startNew(PendingListActivity.class, position);
            } else if (position == 2) {
                startNew(PushNotificationActivity.class, position);
            } else if (position == 3) {
                startNew(AddQuestionActivity.class, position);
            } else if (position == 4) {
                startNew(CompaniesListActivity.class, position);
            } else if (position == 5) {
                startNew(ContactUs.class, position);
            } else if (position == 6) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.logged_user), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("logged_user", null);
                editor.commit();
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }

    }

    private void startNew(Class activity, int position) {
        Log.d("CHECK_START", activity + "");
        Intent intent = new Intent(getActivity(), activity);
        intent.putExtra("position", position);
        intent.putExtra("Title", titles[position]);
        startActivity(intent);
    }
}
