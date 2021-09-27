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

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    EditText eusername, eemail, ephone, epass, econfirmpass;
    Button btn_register;
    TextView txt_alreadyreg;

    String username, email, phoneno, pass, confpass;
    String registerationUrl = Constants.URL_string+"user_request_handler.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        eusername = findViewById(R.id.regusr_uname);
        eemail = findViewById(R.id.regusr_email);
        ephone = findViewById(R.id.regusr_mobile);
        epass = findViewById(R.id.regusr_password);
        econfirmpass = findViewById(R.id.regusr_confpassword);
        btn_register = findViewById(R.id.regusr_btn_submit);
        txt_alreadyreg = findViewById(R.id.regusr_txt_alreadyreg);

        btn_register.setOnClickListener(this);
        txt_alreadyreg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v == btn_register)
        {
            username = eusername.getText().toString().trim();
            email = eemail.getText().toString().trim();
            phoneno = ephone.getText().toString().trim();
            pass = epass.getText().toString().trim();
            confpass = econfirmpass.getText().toString().trim();

            if(!checkIfEmpty() && isPassEqual())
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("contacting server...");
                progressDialog.show();

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, registerationUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if(!object.getBoolean("error"))
                            {
                                Toast.makeText(RegisterUser.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegisterUser.this, UserLogin.class);
                                startActivity(i);
                                finish();
                            }
                            else Toast.makeText(RegisterUser.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //result from server

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("request_type","registration");
                        params.put("username",username);
                        params.put("email",email);
                        params.put("phoneno",phoneno);
                        params.put("password",pass);
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }
            else if(checkIfEmpty())Toast.makeText(this,"please fill all fields",Toast.LENGTH_SHORT).show();
            else if(!isPassEqual())Toast.makeText(this,"password mismatch",Toast.LENGTH_SHORT).show();
        }
        else if(v == txt_alreadyreg)
        {
            Intent i = new Intent(this, UserLogin.class);
            startActivity(i);
            finish();
        }
    }

    private boolean checkIfEmpty(){
        return username.equals("") || email.equals("") || phoneno.equals("") || pass.equals("") || confpass.equals("");
    }

    private boolean isPassEqual(){
        return pass.equals(confpass);
    }
}

