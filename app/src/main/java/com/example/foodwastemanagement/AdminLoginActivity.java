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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends AppCompatActivity
{
    EditText edttxt_username, edttxt_password;
    TextView txt_movetouserlogin;
    Button btn_login;
    String url = Constants.URL_string + "admin_request_handler.php";
    String username,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        edttxt_username = findViewById(R.id.admin_login_edittxt_username);
        edttxt_password = findViewById(R.id.admin_login_edittxt_password);
        btn_login= findViewById(R.id.admin_login_btn_login);
        txt_movetouserlogin = findViewById(R.id.admin_login_txt_movetouserlogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        txt_movetouserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this,UserLogin.class));
                AdminLoginActivity.this.finish();
            }
        });
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
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("error"))
                    {
                        // error occured
                        Toast.makeText(AdminLoginActivity.this,"invalid username or password",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // login successfull
                        Toast.makeText(AdminLoginActivity.this, "login successful ", Toast.LENGTH_SHORT).show();
                        //saveLoginDetails();
                        Intent i = new Intent(AdminLoginActivity.this,AdminDashboardActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminLoginActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

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

    private void saveLoginDetails()
    {
        /*SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("who","admin");
        editor.putString("id",username);
        editor.putString("password",password);
        editor.apply();*/
    }
}

