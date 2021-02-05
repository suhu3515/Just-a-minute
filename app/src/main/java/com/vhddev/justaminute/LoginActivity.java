package com.vhddev.justaminute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    TextView tv_create, tv_skip;
    EditText et_mob, et_pass;
    String mobile, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.login_btn_login);
        tv_create = findViewById(R.id.login_text_register);
        tv_skip = findViewById(R.id.login_skip_login);
        et_mob = findViewById(R.id.login_mobile_no);
        et_pass = findViewById(R.id.login_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate_fields();

            }
        });

        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent createIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(createIntent);
                finish();
                
            }
        });

    }

    private void validate_fields()
    {

        if (et_mob.getText().toString().isEmpty())
        {
            et_mob.setError("Please enter your number");
            et_mob.requestFocus();
        }
        else if (et_mob.getText().toString().length()<10)
        {
            et_mob.setError("Enter 10 digit mobile number");
            et_mob.requestFocus();
        }
        else if (et_pass.getText().toString().isEmpty())
        {
            et_pass.setError("Please enter your password!");
            et_pass.requestFocus();
        }
        else
        {
            loginToApp();
        }
    }

    private void loginToApp()
    {
        mobile = et_mob.getText().toString().trim();
        password = et_pass.getText().toString().trim();

        class UserLogin extends AsyncTask<Void, Void, String>
        {

            private ProgressBar progress_login;

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("login_mobile", mobile);
                params.put("login_pass", password);

                return requestHandler.sendPostRequest(URLs.URL_LOGIN,params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progress_login = (ProgressBar) findViewById(R.id.login_progress);
                progress_login.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                progress_login.setVisibility(View.GONE);

                try
                {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("USER");

                        User user = new User(
                                userJson.getInt("user_id"),
                                userJson.getString("user_name"),
                                userJson.getString("user_gender"),
                                userJson.getString("user_mobile"),
                                userJson.getString("user_pass")
                        );

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        loginToHome();

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,obj.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void loginToHome()
    {
        Intent homeIntent = new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}