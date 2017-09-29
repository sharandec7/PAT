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
 * Created by DETECTIVE7 on 12-04-2016.
 */
public class BranchDataAdapter extends ArrayAdapter {

    Context mContext;
    List<String> branches = new ArrayList<>();

    public BranchDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    public BranchDataAdapter(Context context, List<String> branches) {
        super(context, R.layout.spinner_item, branches);
        mContext = context;
        this.branches = branches;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @Override
    public int getCount() {
        return branches.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(mContext);
        convertView = inflator.inflate(R.layout.spinner_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.spinner_text);
        tv.setText(branches.get(position));
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

}


