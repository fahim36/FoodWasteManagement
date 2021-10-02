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
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity implements View.OnClickListener{

    EditText edittxt_phoneno,edittxt_pass;
    TextView txt_register, txt_gotoadminlogin;
    Button btn_login;
    String phoneno, password;
    SessionHelper sessionHelper;

    String url = Constants.URL_string+"user_request_handler.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        edittxt_phoneno = findViewById(R.id.usr_edittxt_phoneno);
        edittxt_pass = findViewById(R.id.usr_edittxt_password);
        btn_login = findViewById(R.id.usr_btn_login);
        txt_register = findViewById(R.id.usr_txt_register);
        txt_gotoadminlogin = findViewById(R.id.usr_txt_gotoadminlogin);

        btn_login.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        txt_gotoadminlogin.setOnClickListener(this);

        sessionHelper=new SessionHelper(this);

    }

    @Override
    public void onClick(View v)
    {
        if(v == btn_login)
        {
            phoneno = edittxt_phoneno.getText().toString().trim();

            password = edittxt_pass.getText().toString().trim();

            if(phoneno.equals("") || password.equals("")) Toast.makeText(this,"please fill all fields",Toast.LENGTH_SHORT).show();
            else
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("contacting server...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        // result from server
                        UserLoginModel object = new Gson().fromJson(response, UserLoginModel.class);
                        if (object != null) {
                            if (object.getError().equalsIgnoreCase("error")) {
                                Toast.makeText(UserLogin.this, "login error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserLogin.this, "login successfully ", Toast.LENGTH_SHORT).show();
                                UserLoginDataModel model = object.getUserLoginDataModel();
                                sessionHelper.setUserId(model.getUserid());
                                sessionHelper.setUserName(model.getName());
                                sessionHelper.setUserType(model.getType());
                                Intent i = new Intent(UserLogin.this, UserDashboardActivity.class);
                                i.putExtra("phoneno", phoneno);
                                startActivity(i);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(UserLogin.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("request_type","login");
                        params.put("phoneno",phoneno);
                        params.put("password",password);
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }



        }
        else if(v == txt_register)
        {
            Intent i = new Intent(this,RegisterUser.class);
            i.putExtra("userType", "member");
            startActivity(i);
            finish();
        }
        else if(v == txt_gotoadminlogin)
        {
            Intent i = new Intent(this, NGOLoginActivity.class);
            startActivity(i);
            finish();
        }

    }





}
