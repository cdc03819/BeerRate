package newsfeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.greenbeast.beerrate.R;
import com.example.greenbeast.beerrate.SettingsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class add extends AppCompatActivity {
    ImageButton trendingBtm, userBtm, locationBtm, settingsBtm;
    EditText beerid, review;
    String beerName;
    String useridstring;
    String userid;
    String postinfo;
    String postrating;
    String newurl;
    EditText location;
    RatingBar stars;
    Toolbar toolbar;
    FloatingActionButton fab;
    String userlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveinfo(view);
                Snackbar.make(view, "Review Added!", Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(add.this, newsfeed.MainActivity.class));


            }
        });

        trendingBtm = (ImageButton) findViewById(R.id.imageBtmtrending);
        userBtm = (ImageButton) findViewById(R.id.imageBtmuser);
        locationBtm = (ImageButton) findViewById(R.id.imageBtmlocation);
        settingsBtm = (ImageButton) findViewById(R.id.imageBtmSettings);
        trendingBtm.setOnClickListener(btTrending);
        userBtm.setOnClickListener(btUser);
        locationBtm.setOnClickListener(btlocation);
        settingsBtm.setOnClickListener(btSettings);
        beerid = (EditText)findViewById(R.id.reviewTitle);
        review = (EditText)findViewById(R.id.reviewContents);
        location = (EditText)findViewById(R.id.editLocation);
        stars = (RatingBar)findViewById(R.id.ratingBar);

    }

    ImageButton.OnClickListener btTrending = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(add.this, newsfeed.MainActivity.class));
        }

    };
    ImageButton.OnClickListener btUser = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(add.this, newsfeed.user.class));
        }

    };
    ImageButton.OnClickListener btlocation = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(add.this, MapsActivity.class));
        }

    };
    ImageButton.OnClickListener btSettings = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(add.this, SettingsActivity.class));
        }

    };

    public void saveinfo(View view) {

        userid = Users.userID;
        useridstring = String.valueOf(userid);
        beerName = "Beer Name: " + beerid.getText().toString();
        postinfo = review.getText().toString();
        userlocation = location.getText().toString();
        postrating = "Rating: " + String.valueOf(stars.getRating());
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(beerName, postinfo, postrating, userlocation, useridstring);



    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String add_info_url;

        @Override
        protected void onPreExecute() {
            add_info_url = "http://lincoln.sjfc.edu/~cdc03819/CSCI375/addreview.php";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... arg) {
            String beerName, postinfo, postrating, userLocation, userid;

            beerName = arg[0];
            postinfo = arg[1];
            postrating = arg[2];
            userLocation = arg[3];
            userid = arg[4];


            try {
                String data_String = URLEncoder.encode("beerName", "UTF-8") + "=" + URLEncoder.encode(beerName, "UTF-8") + "&" +
                        URLEncoder.encode("postInfo", "UTF-8") + "=" + URLEncoder.encode(postinfo, "UTF-8") + "&" +
                        URLEncoder.encode("postrating", "UTF-8") + "=" + URLEncoder.encode(postrating, "UTF-8")+ "&" +
                        URLEncoder.encode("userLocation", "UTF-8") + "=" + URLEncoder.encode(userLocation, "UTF-8")+ "&" +
                        URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                newurl = add_info_url +"?" + data_String;
                URL url = new URL(newurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();

                Log.d("beerName = ", beerName);
                Log.d("postinfo = ", postinfo);
                Log.d("userLocation = ", userLocation);
                Log.d("postrating = ", postrating);
                Log.d("userID = ", userid);
                Log.d("url = ", newurl);
                return "Data has been inserted successfuly";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}



