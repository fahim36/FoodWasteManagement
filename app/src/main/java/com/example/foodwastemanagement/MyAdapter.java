package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodwastemanagement.model.ListItemModel;

import java.util.ArrayList;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<ListItemModel> list;
    private OnItemClickListener listener;

    public MyAdapter(ArrayList<ListItemModel> listItemModels, Context c,OnItemClickListener listener)
    {
        list = listItemModels;
        context = c;
        this.listener=listener;
    }

    public void addAll(ArrayList<ListItemModel> listItemModels)
    {
        list = listItemModels;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_row,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int pos)
    {
        final int position = pos;
        final MyViewHolder holder = myViewHolder;
        holder.txtview_name.setText(list.get(position).getName());
        holder.txtview_fooddesc.setText(list.get(position).getFooddesc());

        holder.imgview_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     String mapurl;
                    String lat = list.get(position).getLatitude();
                    String lon = list.get(position).getLongitude();

                    mapurl = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;

                Intent maps = new Intent(Intent.ACTION_VIEW, Uri.parse(mapurl));
                maps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.getApplicationContext().startActivity(maps);
            }
        });

       /* holder.imgview_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to delete this item from you record ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(position);
                            }
                        })
                        .setNegativeButton("no", null)
                        .setCancelable(false)
                        .create();
                dialog.show();
            }
        });*/
        holder.imgview_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("do you want to call " + list.get(position).getName() + " ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String phoneNo = list.get(position).getPhoneno();
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("no", null)
                        .create();
                alertDialog.show();
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(
                v, list.get(position), position));

    }

    @Override
    public int getItemCount() {return list.size();}



     class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imgview_location, imgview_call, imgview_delete;
        TextView txtview_name, txtview_fooddesc,rview_tv_accept;

         MyViewHolder(View itemView)
        {
            super(itemView);
            imgview_location = itemView.findViewById(R.id.rview_iv_location);
            imgview_call = itemView.findViewById(R.id.rview_iv_call);
//            imgview_delete = itemView.findViewById(R.id.rview_iv_delete);
            txtview_fooddesc = itemView.findViewById(R.id.rview_tv_fooditem);
            txtview_name = itemView.findViewById(R.id.rview_tv_name);
            rview_tv_accept = itemView.findViewById(R.id.rview_tv_accept);

        }
    }
}
