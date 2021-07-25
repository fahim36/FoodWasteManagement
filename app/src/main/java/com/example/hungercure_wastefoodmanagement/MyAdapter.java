package com.example.hungercure_wastefoodmanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final Context context;
    private ArrayList<ListItem> list;

    public MyAdapter(ArrayList<ListItem> listItems,Context c)
    {
        list = listItems;
        context = c;
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
                if (list.get(position).getLocationtext() == null) {
                    String lat = list.get(position).getLatitude();
                    String lon = list.get(position).getLongitude();

                    mapurl = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;

                } else {
                    mapurl = "https://www.google.com/maps/search/?api=1&query=" + list.get(position).getLocationtext();
                }
                Intent maps = new Intent(Intent.ACTION_VIEW, Uri.parse(mapurl));
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
                                String phoneno = list.get(position).getPhoneno();
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneno));
                                context.getApplicationContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton("no", null)
                        .create();
                alertDialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage(list.get(position).getName()+"\n"+
                                    list.get(position).getFooddesc())
                        .setPositiveButton("ok",null)
                        .create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {return list.size();}



     class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imgview_location, imgview_call, imgview_delete;
        TextView txtview_name, txtview_fooddesc;

         MyViewHolder(View itemView)
        {
            super(itemView);
            imgview_location = itemView.findViewById(R.id.rview_iv_location);
            imgview_call = itemView.findViewById(R.id.rview_iv_call);
//            imgview_delete = itemView.findViewById(R.id.rview_iv_delete);
            txtview_fooddesc = itemView.findViewById(R.id.rview_tv_fooditem);
            txtview_name = itemView.findViewById(R.id.rview_tv_name);

        }
    }
}
