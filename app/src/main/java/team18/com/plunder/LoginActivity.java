package team18.com.plunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import team18.com.plunder.utils.HuntCardData;
import team18.com.plunder.utils.LoginRequest;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Validator;
import team18.com.plunder.utils.VariableBank;

public class LoginActivity extends AppCompatActivity {

    private final String LOGIN_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MapUtil.requestPermissions(this);

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button bSignIn = (Button) findViewById(R.id.bSignIn);

        final TextView tvRegister = (TextView) findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }

        });

        bSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                final Validator validator = new Validator();
                validator.validateLogin(etEmail, etPassword);
                if (isNetworkAvailable()) {
                    if(validator.isValid()) {
                        final View view = v;
                        final String email = etEmail.getText().toString();
                        final String password = etPassword.getText().toString();
                        final AlertDialog warning = new AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Email or Password is incorrect")
                                .setNegativeButton("Retry", null)
                                .create();


                        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
                            ProgressDialog dialog = ProgressDialog.show(view.getContext(), "",
                                    "Loging you in...", true);
                            @Override
                            protected Void doInBackground(Integer... params) {

                                dialog.show();

                                OkHttpClient client = new OkHttpClient();
                                RequestBody formBody = new FormBody.Builder()
                                        .add("email", email)
                                        .add("password", password)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(LOGIN_URL)
                                        .post(formBody)
                                        .build();

                                try {
                                    okhttp3.Response response = client.newCall(request).execute();

                                    JSONObject obj = new JSONObject(response.body().string());
                                    Boolean success = obj.getBoolean("success");
                                    VariableBank.USER_ID = obj.getString("user_id");
                                    VariableBank.NAME = obj.getString("name");
                                    VariableBank.EMAIL = obj.getString("email");
                                    VariableBank.DOB = new Date(Long.parseLong(obj.getString("birth_date")));

                                    if (success) {
                                        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        MainIntent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAP);
                                        LoginActivity.this.startActivity(MainIntent);
                                    } else {
                                        //warning.show();
                                        Snackbar.make(view, "Email or Password is incorrect", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    // End of content reached
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                dialog.hide();
                            }
                        };

                        task.execute();
                    }
                } else {
                    // No network connection
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("No network connection available")
                            .setMessage("Please check that you are connected to either a WI-FI or mobile netowrk.")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();

                }

                /*
                Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                MainIntent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAP);
                LoginActivity.this.startActivity(MainIntent);*/
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}

