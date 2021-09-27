package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NGODashboardActivity extends AppCompatActivity
{

    RecyclerView recyclerView;
    ArrayList<ListItem> list;
    MyAdapter adapter;
    private Paint p = new Paint();
    private String url = Constants.URL_string+"admin_request_handler.php";
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    View viewforsnackbar;
    ArrayList<ListItem> rlist;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();
        rlist = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getAndLoadData();
        viewforsnackbar = findViewById(android.R.id.content);

    }
    public void showPopup(View v) {

        //todo menu button add

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.list_menu, popup.getMenu());
        popup.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.mnu_logout:

                /*SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();*/
                startActivity(new Intent(this, NGOLoginActivity.class));
                finish();
                return true;

            case R.id.mnu_savechanges:

                saveChangesOnServer();
                return true;

            case R.id.mnu_refresh:
                list.clear();
                getAndLoadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void saveChangesOnServer()
    {

        //todo bugfix

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("saving on server...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // if rlist is empty then return
        if(rlist.size() == 0){progressDialog.dismiss(); return;}

        // getting jsonarry from rlist
        Gson gson = new Gson();
        final JsonArray jsonarray = new JsonArray();
        for(ListItem i : rlist)
        {
            String jsonobj = gson.toJson(i);
            jsonarray.add(jsonobj);
        }
        Log.d("saiful",jsonarray.toString());


        //sending request and getting response using volley
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.getBoolean("error"))
                    {
                        Toast.makeText(NGODashboardActivity.this,"saved successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(NGODashboardActivity.this, "some error has occured", Toast.LENGTH_SHORT).show();
                    Log.d("response",object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NGODashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","removerows");
                params.put("removeditemslist",jsonarray.toString());
                return params;
            }
        };
        queue.add(request);
    }




    private void getAndLoadData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("contacting server...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final Gson gson = new Gson();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Log.d("listfw",response);
                    if(object.getJSONArray("list").get(0) == null)Log.d("listfw","object is null");
                    else Log.d("listfw","object not null " +object.getJSONArray("list").get(0).toString());

                    if(!object.getBoolean("error"))
                    {
                        if(object.getString("list").equals("[[]]")){
                            Toast.makeText(NGODashboardActivity.this,"List is empty !",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            JSONArray jsonArray = object.getJSONArray("list");

                            for(int i = 0; i< jsonArray.length(); i++)
                            {
                                ListItem item = gson.fromJson(jsonArray.get(i).toString(),ListItem.class);
                                list.add(item);
                            }
                            adapter = new MyAdapter(list, NGODashboardActivity.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            createNotificationChannel();


                            initSwipe();
                        }

                    }
                    else Toast.makeText(NGODashboardActivity.this,"something went wrong",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NGODashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","getlist");
                return params;
            }
        };

        queue.add(request);
    }



    private void initSwipe()
    {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT)
                {
                    //delete user from list
                    final ListItem backedupitem = list.get(position);
                    list.remove(position);
                    rlist.add(backedupitem);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, list.size());


                    Snackbar snackbar = Snackbar.make(viewforsnackbar,"Do you want to undelete ?",Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            list.add(position,backedupitem);
                            rlist.remove(backedupitem);
                            adapter.notifyItemInserted(position);
                        }
                    });
                    snackbar.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                // Snackbar closed on its own
                                //todo delete method here
                                Toast.makeText(NGODashboardActivity.this,"Success",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



   /* private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }*/


   private void getToken() {

       FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
           @Override
           public void onComplete(@NonNull Task<String> task) {
               //If task is failed then
               if (!task.isSuccessful()) {
                   Log.d(TAG, "onComplete: Failed to get the Token");
               }

               //Token
               String token = task.getResult();
               Log.d(TAG, "onComplete: " + token);
           }
       });
   }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receive Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            getToken();
        }
    }

}

