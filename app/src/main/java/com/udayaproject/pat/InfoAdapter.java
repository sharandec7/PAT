package com.udayaproject.pat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;

/**
 * Created by DETECTIVE7 on 05-12-2015.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyViewHolder> {

    Context context;
    private LayoutInflater inflater;
    List<DriveItem> data = Collections.emptyList();
    private MyRecyclerClickListener myrecyclerclicklistener;

    public InfoAdapter(Context context, List<DriveItem> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void updateList(List<DriveItem> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DriveItem current = data.get(position);
        String[] separated = current.oraganization.split(":");
        String organization = separated[1];
        holder.title.setText(organization+" Drive");
        holder.icon.setImageResource(current.iconId);
        holder.subtitle.setText(" Package: "+current.salary);
        holder.date.setText(current.date);

    }

    public void setRecyclerItemClickListener(MyRecyclerClickListener myrecyclerclicklistener) {
        this.myrecyclerclicklistener = myrecyclerclicklistener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, subtitle, date;
        ImageView icon;

        public MyViewHolder(View itemView, Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listHeadText);
            subtitle = (TextView) itemView.findViewById(R.id.listSubText);
            date = (TextView) itemView.findViewById(R.id.listDateText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myrecyclerclicklistener != null) {
                myrecyclerclicklistener.recyclerItemClicked(v, getPosition());
            }
        }
    }

    public interface MyRecyclerClickListener {
         void recyclerItemClicked(View view, int position);
    }
}

