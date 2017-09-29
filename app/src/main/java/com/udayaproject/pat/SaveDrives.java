package com.udayaproject.pat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DETECTIVE7 on 25-03-2016.
 */
public class SaveDrives {


    static List<CompanyItem> company_items = new ArrayList<>();
    static List<DriveItem> drive_items = new ArrayList<>();
    static List<QuestionItem> q_items = new ArrayList<>();
    static List<StudentItem> s_items = new ArrayList<>();
    static int icon = R.mipmap.place_me;
    private static List<String> college_items = new ArrayList<String>();

    static List<DriveItem> getData(Context context) {

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.drives), context.MODE_PRIVATE);
        String result = sp.getString("drives_json", null);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("drives");


            //JSONArray success_result  = new JSONArray(result);
            //Log.d("SUCCESS_FAIL",success_result.toString());

            drive_items.removeAll(drive_items);
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String id = post.getString("id");
                String organization = post.optString("o");
                String designation = post.optString("d");
                String salary = post.optString("p");
                String criteria = post.optString("c");
                String branch = post.optString("b");
                String date = post.optString("date");
                String venue = post.optString("v");

                DriveItem current = new DriveItem();

                current.oraganization = organization;
                current.designation = designation;
                current.salary = salary;
                current.criteria = criteria;
                current.branch = branch;
                current.date = date;
                current.venue = venue;
                current.iconId = icon;

                drive_items.add(current);
                Log.d("ORGANIZATION", organization);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return drive_items;
    }

    static List<StudentItem> getPendingData(Context context) {

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.pending), context.MODE_PRIVATE);
        String result = sp.getString("pending_json", null);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("pending");


            //JSONArray success_result  = new JSONArray(result);
            //Log.d("SUCCESS_FAIL",success_result.toString());

            s_items.removeAll(s_items);
            for (int i = 0; i < posts.length(); i++) {

                StudentItem current = new StudentItem();

                JSONObject post = posts.getJSONObject(i);

                String s_email = post.optString("e");
                String s_name = post.optString("n");
                String s_phone = post.optString("contact");
                String s_college = post.optString("c");
                String s_percentage = post.optString("p");
                String s_backlogs = post.optString("b");
                String s_active = post.optString("a");
                String s_roll= post.optString("r");
                String s_approved = post.optString("ap");
                String s_branch = post.optString("branch");
                String s_yop = post.optString("y");


                current.email = s_email;
                current.name = s_name;
                current.phone = s_phone;
                current.college = s_college;
                current.branch = s_branch;
                current.percentage = s_percentage;
                current.active = s_active;
                current.backlogs = s_backlogs;
                current.roll = s_roll;
                current.approved = s_approved;
                current.yop = s_yop;
                current.iconId = icon;

                Log.d("USER DATA", s_name);
                s_items.add(current);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return s_items;
    }

    static List<CompanyItem> getCompaniesData(Context context) {

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.companies), context.MODE_PRIVATE);
        String result = sp.getString("companies_json", null);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("companies");

            //JSONArray success_result  = new JSONArray(result);
            //Log.d("SUCCESS_FAIL",success_result.toString());

            company_items.removeAll(company_items);
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String id = post.getString("id");
                String name = post.optString("c_n");
                String location = post.optString("l");
                String contact = post.optString("c");
                String email = post.optString("e");
                String description = post.optString("d");
                String tests = post.optString("t");
                String website = post.optString("w");
                String last = post.optString("l_u");

                CompanyItem current = new CompanyItem();

                current.iconId = icon;
                current.company_id = id;
                current.company_name = name;
                current.location = location;
                current.contact = contact;
                current.email = email;
                current.description = description;
                current.tests = tests;
                current.website = website;
                current.last_updated = last;

                company_items.add(current);
                Log.d("ORGANIZATION", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return company_items;
    }


    static List<String> getCollegesData(Context context) {

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.colleges_json), context.MODE_PRIVATE);
        String result = sp.getString("colleges_json", null);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("colleges");

            college_items.removeAll(college_items);
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String id = post.getString("id");
                String name = post.optString("c_n");
                String location = post.optString("l");
                String contact = post.optString("c");
                String email = post.optString("e");

                String current = name;

                college_items.add(current);
                Log.d("COLLEGE", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return college_items;
    }


    public static List<QuestionItem> getQuestionsData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.questions), context.MODE_PRIVATE);
        String result = sp.getString("questions_json", null);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("questions");

            //JSONArray success_result  = new JSONArray(result);
            //Log.d("SUCCESS_FAIL",success_result.toString());

            q_items.removeAll(q_items);
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String id = post.getString("id");
                String question = post.optString("q");
                String option1 = post.optString("o1");
                String option2 = post.optString("o2");
                String option3 = post.optString("o3");
                String option4 = post.optString("o4");
                int answer = Integer.parseInt(post.optString("a"));

                QuestionItem current = new QuestionItem();

                current.q_id = id;
                current.question = question;
                current.option1 = option1;
                current.option2 = option2;
                current.option3 = option3;
                current.option4 = option4;
                current.answer = answer;

                q_items.add(current);
                Log.d("QUESTION", question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return q_items;
    }

    public static LoggedUser getLoggedUserData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.logged_user_data), context.MODE_PRIVATE);
        String result = sp.getString("logged_user_json", null);
        LoggedUser current = new LoggedUser();

        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("user_data");

            for (int i = 0; i < posts.length(); i++)
            {
                JSONObject post = posts.getJSONObject(i);
                String s_name = post.optString("n");
                String s_type = post.optString("t");
                String s_phone = post.optString("contact");
                String s_college = post.optString("c");
                String s_percentage = post.optString("p");
                String s_backlogs = post.optString("b");
                String s_active = post.optString("a");
                String s_roll= post.optString("r");
                String s_approved = post.optString("ap");
                String s_branch = post.optString("branch");


                current.name = s_name;
                current.type = s_type;
                current.contact = s_phone;
                current.college = s_college;
                current.branch = s_branch;
                current.percentage = s_percentage;
                current.active = s_active;
                current.backlogs = s_backlogs;
                current.roll = s_roll;
                current.approved = s_approved;

                Log.d("USER DATA", s_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return current;
    }
}
