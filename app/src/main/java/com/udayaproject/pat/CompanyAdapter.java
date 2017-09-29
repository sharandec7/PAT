package com.udayaproject.pat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DETECTIVE7 on 01-04-2016.
 */
public class CompanyAdapter extends ArrayAdapter{


    Context context;
    List<CompanyItem> companies = new ArrayList<>();

    public CompanyAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CompanyAdapter(Context context, List<CompanyItem> companies) {
        super(context, R.layout.spinner_item, companies);
        this.companies = companies;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @Override
    public int getCount() {
        return companies.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(R.layout.spinner_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.spinner_text);
        tv.setText(companies.get(position).company_name);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

}
