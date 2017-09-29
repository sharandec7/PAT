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
 * Created by DETECTIVE7 on 28-02-2016.
 */
public class DrawerInfoAdapter  extends RecyclerView.Adapter<DrawerInfoAdapter.MyDrawerViewHolder> {

    Context context;
    private LayoutInflater inflater;
    List<Information> data = Collections.emptyList();
    private MyDrawerClickListener mydrawerclicklistener;

    public DrawerInfoAdapter(Context context, List<Information> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_drawer_list_item_layout, parent, false);
        MyDrawerViewHolder holder = new MyDrawerViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyDrawerViewHolder holder, final int position) {
        Information current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
        if(position != 0){
            holder.button.setVisibility(View.INVISIBLE);
        }

    }

    public void setDrawerItemClickListener(MyDrawerClickListener mydrawerclicklistener) {
        this.mydrawerclicklistener = mydrawerclicklistener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon,button;

        public MyDrawerViewHolder(View itemView, Context context) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            button = (ImageView) itemView.findViewById(R.id.green_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mydrawerclicklistener != null) {
                mydrawerclicklistener.drawerItemClicked(v, getPosition());
            }
        }
    }

    public interface MyDrawerClickListener {
         void drawerItemClicked(View view, int position);
    }
}

