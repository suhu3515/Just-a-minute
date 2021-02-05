package com.vhddev.justaminute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText et_name, et_mob, et_pass, et_c_pass;
    TextView tv_login_txt;
    RadioGroup radioGroup;
    String name,gender,mobile,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.register_btn_register);
        et_name = findViewById(R.id.register_name);
        et_mob = findViewById(R.id.register_mobile_no);
        et_pass = findViewById(R.id.register_password);
        et_c_pass = findViewById(R.id.register_confirm_password);
        tv_login_txt = findViewById(R.id.register_text_login);
        radioGroup = findViewById(R.id.register_radio_grp);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate_fields();

            }
        });

        tv_login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backToLogin();

            }
        });

    }

    private void validate_fields()
    {
        if (et_name.getText().toString().isEmpty())
        {
            et_name.setError("Please enter your name");
            et_name.requestFocus();
        }
        else if (et_name.getText().toString().length()<3)
        {
            et_name.getText().clear();
            et_name.setError("Name must have at least 3 letters");
            et_name.requestFocus();
        }
        else if (et_mob.getText().toString().isEmpty())
        {
            et_mob.setError("Please enter your number");
            et_mob.requestFocus();
        }
        else if (et_mob.getText().toString().length()<10)
        {
            et_mob.setError("Enter 10 digit mobile number");
            et_mob.requestFocus();
        }
        else if (!TextUtils.isDigitsOnly(et_mob.getText().toString()))
        {
            et_mob.getText().clear();
            et_mob.setError("Enter the mobile number in digits");
            et_mob.requestFocus();
        }
        else if (et_pass.getText().toString().isEmpty())
        {
            et_pass.setError("Please enter a password");
            et_pass.requestFocus();
        }
        else if (et_pass.getText().toString().length()<4 || et_pass.getText().toString().length()>16)
        {
            et_pass.getText().clear();
            et_pass.setError("Password must be between 4 and 16 characters");
            et_pass.requestFocus();
        }
        else if (!et_c_pass.getText().toString().equals(et_pass.getText().toString()))
        {
            et_c_pass.setError("Both passwords must be same!");
            et_c_pass.requestFocus();
        }
        else
        {
            registerUser();
        }
    }

    private void registerUser()
    {
        name = et_name.getText().toString().trim();
        gender = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();
        mobile = et_mob.getText().toString().trim();
        if (et_c_pass.getText().toString().equals(et_pass.getText().toString()))
        {
            password = et_c_pass.getText().toString().trim();
        }

        class RegisterUser extends AsyncTask<Void,Void, String>
        {
            private ProgressBar progressBar;


            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("user_name",name);
                params.put("user_gender",gender);
                params.put("user_mobile", mobile);
                params.put("user_password", password);

                return requestHandler.sendPostRequest(URLs.URL_REGISTER,params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar = (ProgressBar) findViewById(R.id.register_progress);
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                progressBar.setVisibility(View.GONE);

                try
                {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error"))
                    {
                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(
                                userJson.getInt("user_id"),
                                userJson.getString("user_name"),
                                userJson.getString("user_gender"),
                                userJson.getString("user_mobile"),
                                userJson.getString("user_pass")
                        );

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        backToLogin();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void backToLogin()
    {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}