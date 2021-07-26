package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity
{

    RecyclerView recyclerView;
    ArrayList<ListItem> list;
    MyAdapter adapter;
    private Paint p = new Paint();
    private String url = Constants.URL_string+"admin_request_handler.php";
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
                startActivity(new Intent(this,AdminLoginActivity.class));
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
                        Toast.makeText(AdminDashboardActivity.this,"saved successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(AdminDashboardActivity.this, "some error has occured", Toast.LENGTH_SHORT).show();
                    Log.d("response",object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminDashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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
                    Log.d("listrs",response);
                    if(object.getJSONArray("list").get(0) == null)Log.d("listrs","object is null");
                    else Log.d("listrs","object not null " +object.getJSONArray("list").get(0).toString());

                    if(!object.getBoolean("error"))
                    {
                        if(object.getString("list").equals("[[]]")){
                            Toast.makeText(AdminDashboardActivity.this,"user list is empty !",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            JSONArray jsonArray = object.getJSONArray("list");

                            for(int i = 0; i< jsonArray.length(); i++)
                            {
                                ListItem item = gson.fromJson(jsonArray.get(i).toString(),ListItem.class);
                                list.add(item);
                            }
                            adapter = new MyAdapter(list,AdminDashboardActivity.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                            initSwipe();
                        }

                    }
                    else Toast.makeText(AdminDashboardActivity.this,"something went wrong",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminDashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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
//                    adapter.removeItem(position);
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
                    snackbar.show();



                } /*else {


                   // make phone call to  user
                    String phoneno = list.get(position).getPhoneno();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneno));
                    startActivity(intent);
                }*/
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
/*
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
                {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0)
                    {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(AdminDashboardActivity.this,R.drawable.ic_call_white);
                        assert drawable != null;
                        icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_call_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        if(icon!=null)c.drawBitmap(icon,null,icon_dest,p);
                    }
                    else
                        {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_blue);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        if(icon!=null)c.drawBitmap(icon,null,icon_dest,p);
                    }
                }*/
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


}

