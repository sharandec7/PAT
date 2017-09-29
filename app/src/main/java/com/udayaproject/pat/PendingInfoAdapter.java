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
 * Created by DETECTIVE7 on 12-04-2016.
 */
public class PendingInfoAdapter extends RecyclerView.Adapter<PendingInfoAdapter.MyViewHolder> {

    Context context;
    private LayoutInflater inflater;
    List<StudentItem> data = Collections.emptyList();
    private MyRecyclerClickListener myrecyclerclicklistener;

    public PendingInfoAdapter(Context context, List<StudentItem> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void updateList(List<StudentItem> data) {
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
        StudentItem current = data.get(position);
        holder.title.setText(current.name);
        holder.icon.setImageResource(current.iconId);
        holder.subtitle.setText("Phone: " + current.phone);
        holder.date.setText(current.yop);
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



