package com.example.foodwastemanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.foodwastemanagement.model.UserLoginDataModel;
import com.example.foodwastemanagement.model.UserLoginModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NGOLoginActivity extends AppCompatActivity
{
    EditText edttxt_username, edttxt_password;
    TextView txt_movetouserlogin,usr_txt_register;
    Button btn_login;
    String url = Constants.URL_string + "admin_request_handler.php";
    String username,password;

    SessionHelper sessionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        edttxt_username = findViewById(R.id.admin_login_edittxt_username);
        edttxt_password = findViewById(R.id.admin_login_edittxt_password);
        btn_login= findViewById(R.id.admin_login_btn_login);
        usr_txt_register= findViewById(R.id.usr_txt_register);
        txt_movetouserlogin = findViewById(R.id.admin_login_txt_movetouserlogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        usr_txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(NGOLoginActivity.this,RegisterUser.class);
                    i.putExtra("userType", "ngo");
                    startActivity(i);
                    finish();
            }
        });


        txt_movetouserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NGOLoginActivity.this,UserLogin.class));
                NGOLoginActivity.this.finish();
            }
        });

        sessionHelper=new SessionHelper(this);
    }

    private void sendRequest() {

        username = edttxt_username.getText().toString().trim();
        password = edttxt_password.getText().toString().trim();

        if(username.equals("")|| password.equals(""))
        {
            Toast.makeText(this,"please fill all the fields ",Toast.LENGTH_SHORT).show();
            return;
        }

        //showing progressdialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("contacting server...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                UserLoginModel object = new Gson().fromJson(response, UserLoginModel.class);
                if(object.getError().equalsIgnoreCase("error"))
                {
                    // error occured
                    Toast.makeText(NGOLoginActivity.this,"invalid username or password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // login successfull

                    Toast.makeText(NGOLoginActivity.this, "login successful ", Toast.LENGTH_SHORT).show();
                    UserLoginDataModel model = object.getUserLoginDataModel();
                    sessionHelper.setUserId(model.getUserid());
                    sessionHelper.setUserName(model.getName());
                    sessionHelper.setUserType(model.getType());
                    sessionHelper.setFirebaseToken(model.getUsertoken()!=null ? model.getUsertoken() : "");
                    Intent i = new Intent(NGOLoginActivity.this, NGODashboardActivity.class);
                    startActivity(i);
                    finish();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NGOLoginActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("request_type","login");
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };

        queue.add(stringRequest);
    }

}

