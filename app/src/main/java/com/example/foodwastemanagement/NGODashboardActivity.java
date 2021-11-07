package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.foodwastemanagement.model.ListItemModel;
import com.example.foodwastemanagement.model.NGOPushModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NGODashboardActivity extends AppCompatActivity
{

    RecyclerView recyclerView;
    ArrayList<ListItemModel> list;
    MyAdapter adapter;
    private Paint p = new Paint();
    private String url = Constants.URL_string+"admin_request_handler.php";
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    View viewforsnackbar;
    SessionHelper sessionHelper;
    ArrayList<ListItemModel> rlist;


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

        sessionHelper=new SessionHelper(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RemoteMessage remoteMessage) {
       //todo
        if(!sessionHelper.getUserType().equalsIgnoreCase("ngo"))
            return;

        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        NGOPushModel model = new Gson().fromJson(remoteMessage.getData().get("data"),NGOPushModel.class);

        DialogWithButtons dialog = new DialogWithButtons(this, new DialogWithButtons.OnDialogButtonClickListener() {
            @Override
            public void onPositiveClicked(@NonNull DialogWithButtons d) {
                        d.dismiss();

            }

            @Override
            public void onNegativeClicked(@NonNull DialogWithButtons d) {
                ListItemModel listItemModel = new ListItemModel();
                listItemModel.setPickupid(model.getPickupid());
                rlist.add(listItemModel);
                saveChangesOnServer();
                d.dismiss();
            }

            @Override
            public void onNeutralClicked(@NonNull DialogWithButtons d) {
                d.dismiss();
            }
        });

        dialog.show();
        dialog.setTitle("Pickup Alert");
        dialog.setSubtitle("You have a new pickup request in your area");
        dialog.setCancelable(false);
    }
    private void showNotification(String title,String message){

        Intent intent = new Intent(this, NGODashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
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

        // if rlist is empty then return
        if(rlist.size() == 0){return;}

        String pickupid=rlist.get(0).getPickupid();


        //sending request and getting response using volley
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.getBoolean("error"))
                    {
       //                 Toast.makeText(NGODashboardActivity.this,"Saved Successfully",Toast.LENGTH_SHORT).show();
                        getAndLoadData();
                    }
                    else {
                        Toast.makeText(NGODashboardActivity.this, "some error has occured", Toast.LENGTH_SHORT).show();

                        Log.d("response", object.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NGODashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","cancelpickuprequest");
                params.put("pickupid",pickupid);
                params.put("ngoid",sessionHelper.getUserId());
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
                            list=new ArrayList<>();
                            Toast.makeText(NGODashboardActivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                            for(int i = 0; i< jsonArray.length(); i++)
                            {
                                ListItemModel item = gson.fromJson(jsonArray.get(i).toString(), ListItemModel.class);
                                if(item.getPickupStatus()!=null){
                                    if(!item.getPickupStatus().equalsIgnoreCase("Accepted"))
                                        list.add(item);
                                }else{
                                        list.add(item);
                                }

                            }
                            adapter = new MyAdapter(list, NGODashboardActivity.this, (view, item, position) -> {
                                ListItemModel listItemModel = (ListItemModel) item;
                                acceptPickupRequest(listItemModel);

                            });
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
                params.put("ngoid",sessionHelper.getUserId());
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
                    final ListItemModel backedupitem = list.get(position);
                    list.remove(position);
                    rlist.add(backedupitem);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, list.size());

                    DialogWithButtons dialog = new DialogWithButtons(NGODashboardActivity.this, new DialogWithButtons.OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClicked(@NonNull DialogWithButtons d) {
                            saveChangesOnServer();
                            Toast.makeText(NGODashboardActivity.this,"Success",Toast.LENGTH_SHORT).show();
                            d.dismiss();
                        }
                        @Override
                        public void onNegativeClicked(@NonNull DialogWithButtons d) {
                            list.add(position,backedupitem);
                            rlist.remove(backedupitem);
                            adapter.notifyItemInserted(position);
                            d.dismiss();
                        }

                        @Override
                        public void onNeutralClicked(@NonNull DialogWithButtons d) {
                            d.dismiss();
                        }
                    });

                    dialog.show();
                    dialog.setTitle("Confirmation");
                    dialog.setPositiveButtonLabel("Yes");
                    dialog.setNegativeButtonLabel("No");
                    dialog.setSubtitle("Do you really want to cancel this pickup request ?");
                    dialog.setCancelable(false);

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
               updateToken(token);
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
        }
        getToken();
    }
    private  void updateToken(String token){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("contacting server...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Log.d(TAG, "Token:" + object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NGODashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request_type", "update_token");
                params.put("token", token);
                params.put("userid", sessionHelper.getUserId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void acceptPickupRequest(ListItemModel model)
    {

        //sending request and getting response using volley
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.getBoolean("error"))
                    {
                        Toast.makeText(NGODashboardActivity.this,"Successfully accepted the request",Toast.LENGTH_SHORT).show();
                        getAndLoadData();
                    }
                    else {
                        Toast.makeText(NGODashboardActivity.this, "some error has occured", Toast.LENGTH_SHORT).show();

                        Log.d("response", object.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NGODashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","updatestatus");
                params.put("pickupid",model.getPickupid());
                params.put("pickupstatus","Accepted");
                return params;
            }
        };
        queue.add(request);
    }

}

