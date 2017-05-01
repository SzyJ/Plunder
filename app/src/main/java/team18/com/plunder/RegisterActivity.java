package team18.com.plunder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import team18.com.plunder.utils.RegisterRequest;
import team18.com.plunder.utils.Validator;

public class RegisterActivity extends AppCompatActivity {

    private final String REGISTER_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/register.php";
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
        final EditText etBirthDate = (EditText) findViewById(R.id.etBirthdate);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validator validator = new Validator();
                validator.validateRegister(etName, etEmail, etPassword, etPassword2, etBirthDate);
                if (isNetworkAvailable()) {
                    if (validator.isValid()) {
                        //etName.setError("Pass");
                        final String name = etName.getText().toString();
                        final String email = etEmail.getText().toString();
                        final String password = etPassword.getText().toString();
                        final String birthDate = etBirthDate.getText().toString();
                        final View view = v;

                        Snackbar.make(view, "n:" + name + " e:" + email + " p:" + password + " d:" + birthDate, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action", null).show();
                        /*
                        final AlertDialog warning = new AlertDialog.Builder(RegisterActivity.this)
                                .setMessage("n:" + name + " e:" + email + " p:" + password + " d:" + birthDate)
                                .setNegativeButton("Retry", null)
                                .create();

                        //warning.show();*/
                        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
                            ProgressDialog dialog = ProgressDialog.show(view.getContext(), "",
                                    "Please wait", true);
                            @Override
                            protected Void doInBackground(Integer... params) {

                                dialog.show();

                                OkHttpClient client = new OkHttpClient();
                                RequestBody formBody = new FormBody.Builder()
                                        .add("name", name)
                                        .add("email", email)
                                        .add("birthDate", birthDate)
                                        .add("password", password)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(REGISTER_URL)
                                        .post(formBody)
                                        .build();

                                try {
                                    okhttp3.Response response = client.newCall(request).execute();

                                    JSONObject obj = new JSONObject(response.body().string());
                                    Boolean success = obj.getBoolean("success");

                                    if (success) {
                                        Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        MainIntent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAP);
                                        RegisterActivity.this.startActivity(MainIntent);
                                    } else {
                                        //warning.show();
                                        Snackbar.make(view, "An error has occured, Please try again later", Snackbar.LENGTH_SHORT)
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("No network connection available")
                        .setMessage("Please check that you are connected to either a WI-FI or mobile netowrk.")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();

                }
            }
        });

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                etBirthDate.setText(sdf.format(myCalendar.getTime()));



            }

        };


        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this, datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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
