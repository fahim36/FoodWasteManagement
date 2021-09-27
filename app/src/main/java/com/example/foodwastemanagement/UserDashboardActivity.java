package com.example.foodwastemanagement;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDashboardActivity extends AppCompatActivity {

    EditText edttxt_foodesc, edttxt_loctiondesc;
    TextView newLocationTv;
    CheckBox chkbx_currentlocation;
    Button btn_send;
    String str_locationdesc = null, str_fooddesc = null, url = Constants.URL_string + "user_request_handler.php", str_phoneno;
    String str_latitude, str_longitude;
    boolean bool_usecurrentlocation = false;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

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

        if (!bool_usecurrentlocation)
        {
            str_locationdesc = edttxt_loctiondesc.getText().toString();
            sendRequest();
        }
        else
        {
            //displaying progress
            final ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setMessage("getting your location from gps....\n please be patient.");
            progressDialog1.setCanceledOnTouchOutside(false);
            progressDialog1.show();

            // code for grabing current location
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationListener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    str_longitude = location.getLongitude() + "";
                    str_latitude = location.getLatitude() + "";
                    progressDialog1.dismiss();

                    // sending request..
                    sendRequest();
                    if(mLocationListener!=null)mLocationManager.removeUpdates(mLocationListener);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider)
                {
                    /*mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                            0, this);*/
                }

                @Override
                public void onProviderDisabled(String provider)
                {
                    progressDialog1.dismiss();
                    Toast.makeText(UserDashboardActivity.this, "please enable gps in settings > location", Toast.LENGTH_SHORT).show();
                }
            };


            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    0, mLocationListener);
        }



    }


    private void sendRequest()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("getting your location from gps....\n please be patient.");
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
                        Toast.makeText(UserDashboardActivity.this,"send successfully",Toast.LENGTH_SHORT).show();
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
                params.put("request_type","getuserinfo");
                params.put("food_description",str_fooddesc);
                params.put("phoneno",str_phoneno);
                params.put("iscurrentlocation",bool_usecurrentlocation+"");

                if(bool_usecurrentlocation)
                {
                    params.put("latitude",str_latitude);
                    params.put("longitude",str_longitude);
                }
                else
                {
                    params.put("locationtext",str_locationdesc);
                }

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
}
