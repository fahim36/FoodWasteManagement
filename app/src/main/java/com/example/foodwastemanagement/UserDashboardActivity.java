package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodwastemanagement.model.ListItemModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDashboardActivity extends AppCompatActivity {

    EditText edttxt_foodesc, edttxt_loctiondesc;
    TextView newLocationTv;
    CheckBox chkbx_currentlocation;
    Button btn_send;
    String str_itemDetails = "", str_fooddesc = null, url = Constants.URL_string + "user_request_handler.php", str_phoneno;
    String str_latitude, str_longitude;
    boolean bool_usecurrentlocation = true;
    SessionHelper sessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        edttxt_foodesc = findViewById(R.id.usr_dashboard_edittxt_fooddesc);
        edttxt_loctiondesc = findViewById(R.id.usr_dashboard_edittxt_locationdesc);
        chkbx_currentlocation = findViewById(R.id.usr_dashboard_chkbx_currentlocation);
        btn_send = findViewById(R.id.usr_dashboard_btn_send);
        newLocationTv= findViewById(R.id.newLocationTv);

        sessionHelper=new SessionHelper(this);

        newLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(UserDashboardActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        str_phoneno = getIntent().getStringExtra("phoneno");

        chkbx_currentlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edttxt_loctiondesc.setEnabled(false);
                    edttxt_loctiondesc.setFocusableInTouchMode(false);
                    bool_usecurrentlocation = true;
                }
                else
                {
                    edttxt_loctiondesc.setEnabled(true);
                    edttxt_loctiondesc.setFocusableInTouchMode(true);
                    bool_usecurrentlocation = false;
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabInfo();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void grabInfo()
    {
        str_fooddesc = edttxt_foodesc.getText().toString().trim();

        if(str_fooddesc.isEmpty()){
            Toast.makeText(UserDashboardActivity.this, "Please fill out every input field", Toast.LENGTH_SHORT).show();
            return;
        }

        str_itemDetails = edttxt_loctiondesc.getText().toString();
        str_longitude = sessionHelper.getLastKnownLON();
        str_latitude =  sessionHelper.getLastKnownLAT();
        if(str_latitude!=null || str_longitude!=null)
            sendRequest();

        else{
            Toast.makeText(UserDashboardActivity.this, "Please confirm your location", Toast.LENGTH_SHORT).show();
            newLocationTv.callOnClick();
        }


    }


    private void sendRequest()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Contacting server.\nPlease be patient.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(UserDashboardActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    if(object.getBoolean("error"))
                    {
                        Toast.makeText(UserDashboardActivity.this,"something went wrong....",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(UserDashboardActivity.this,"Pickup request sent successfully",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(UserDashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","addpickuprequest");
                params.put("food_description",str_fooddesc);
                params.put("phoneno",str_phoneno);
                params.put("iscurrentlocation",bool_usecurrentlocation+"");
                params.put("itemdetails", str_itemDetails);
                params.put("latitude",str_latitude);
                params.put("longitude",str_longitude);



                return params;
            }
        };

        queue.add(request);
    }
    //todo
//    public void onGoToMaps(View view){
//        Intent intent = new Intent(getApplicationContext(),PickupLocationActivity.class);
//        Log.i("lat",""+latitude+ longitude);
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude",longitude);
//        startActivity(intent);
//        setResult(Activity.RESULT_OK,intent);
//        finish();
//    }

    private void deleteUserRequest(ArrayList<ListItemModel > rlist)
    {

        //todo getlist

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("saving on server...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // if rlist is empty then return
        if(rlist.size() == 0){progressDialog.dismiss(); return;}

        // getting jsonarry from rlist
        Gson gson = new Gson();
        final JsonArray jsonarray = new JsonArray();
        for(ListItemModel i : rlist)
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
                        Toast.makeText(UserDashboardActivity.this,"Saved Successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(UserDashboardActivity.this, "some error has occured", Toast.LENGTH_SHORT).show();
                    Log.d("response",object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(UserDashboardActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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
}
