package com.example.greenbeast.beerrate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import newsfeed.HttpServicesClass;
import newsfeed.Users;

public class LoginActivity extends AppCompatActivity {
    Button signIn, register;
    EditText email, password;
    String userName, userPassword, msg, splitres;
    String[] parts;
    String add_user_url, login_url, newurl, response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        signIn = (Button) findViewById(R.id.email_sign_in_button);
        register = (Button) findViewById(R.id.email_register_button3);
        signIn.setOnClickListener(btSignIn);
        register.setOnClickListener(btRegister);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

    }

    ImageButton.OnClickListener btSignIn = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveinfo(v);

        }

    };
    ImageButton.OnClickListener btRegister = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, register.class));
        }

    };

    public void saveinfo(View view) {
        userName = email.getText().toString();
        userPassword = password.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(userName, userPassword);
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String add_info_url;

        @Override
        protected void onPreExecute() {
            login_url = "http://lincoln.sjfc.edu/~cdc03819/CSCI375/login.php";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... arg) {
            String userName, userPassword;
            userName = arg[0];
            userPassword = arg[1];
            try {
                String data_String = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("userPassword", "UTF-8") + "=" + URLEncoder.encode(userPassword, "UTF-8");
                newurl = login_url + "?" + data_String;
                URL url = new URL(newurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String response = "";
                //for (login_url) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(newurl);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                    //Log.d("userID = ", String.valueOf(Users.userID));
                    if (response.endsWith("]")) {
                        splitres = response;
                        parts = splitres.split(":");
                        //Users.userID = parts[1];
                        String[] newparts = parts[1].split(",");
                        newparts[0] = newparts[0].substring(1);
                        newparts[0] = newparts[0].replace(newparts[0].substring(newparts[0].length()-1),"");
                        Users.userID = newparts[0];

                        startActivity(new Intent(LoginActivity.this, newsfeed.MainActivity.class));
                        msg = "Login Successful!";
                    } else {
                        msg = "Invalid username or password!";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}


                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();

                Log.d("userName = ", userName);
                Log.d("userPassword = ", userPassword);
                Log.d("url = ", newurl);
                Log.d("valid = ", response);
                //return "Login Successful!";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public Context context;

        String ResultHolder;

        List<Users> subjectsList;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpServicesClass httpServiceObject = new HttpServicesClass(newurl);
            try {
                httpServiceObject.ExecutePostRequest();

                if (httpServiceObject.getResponseCode() == 200) {
                    ResultHolder = httpServiceObject.getResponse();

                    if (ResultHolder != null) {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(ResultHolder);

                            JSONObject jsonObject;

                            Users Users;

                            subjectsList = new ArrayList<Users>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Users = new Users();

                                jsonObject = jsonArray.getJSONObject(i);

                                //Users.userID = jsonObject.getInt("userID");
                                //Users.userID = response.charAt(11);
                                Log.d("userID = ", String.valueOf(Users.userID));

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Toast.makeText(context, httpServiceObject.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }


    }
}



