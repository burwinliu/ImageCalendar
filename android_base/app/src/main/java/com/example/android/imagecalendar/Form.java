package com.example.android.imagecalendar;

import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Form extends AppCompatActivity {
    EditText m_title, m_date, m_startTime, m_endTime, m_description;
    CalendarEvent ce = new CalendarEvent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        String photo_file = intent.getStringExtra("photo_file");
        /*Process data from photo file*/

        m_title=(EditText) findViewById(R.id.title);
        m_date=(EditText) findViewById(R.id.date);
        m_startTime=(EditText) findViewById(R.id.startTime);
        m_endTime=(EditText) findViewById(R.id.endTime);
        m_description=(EditText) findViewById(R.id.description);

        try {
            String sURL = "http://54.188.34.34:8080/get_event?bucket=imagecal&key="+photo_file; //just a string

            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in =new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JSONObject res = new JSONObject(response.toString());
//            JSONObject res = new JSONObject("{\"description\":[\"A sensory- sory-friendly movie night for\",\"people on the spectrum\",\"April 2, 2019 6PM\",\"San Dias Community Hall\",\"Community Hall is wheelchair-accessible.\",\"SAN DIAS COMMUNLTY CENTER\"],\"times\":[\"Tue, 02 Apr 2019 18:00:00 GMT\"],\"title\":\"Lights Up, Sound Down \"}\n");
            JSONArray arr = null;
            try {
                arr = res.getJSONArray("description");
            } catch (Exception e) {
            }
            String desc="";
            for(int i=0; i<arr.length(); i++){
                try {
                    desc+=(arr.getString(i)+"\n");
                } catch (Exception e) {
                }
            }
            ce.setDescription(desc);

            try {
                ce.setTitle(res.getString("title"));
            } catch (Exception e) {
            }
            try {
                ce.setStartTime(res.getJSONArray("times").getString(0));
            } catch (Exception e) {
            }
            //TODO: Parse string into date, start time
            try {
                ce.setEndTime(res.getJSONArray("times").getString(1));
            } catch (Exception e) {
            }
            //TODO: Parse string into date, end time
            try {
                ce.setDate(res.getJSONArray("times").getString(0));
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }

        m_title.setText(ce.getTitle());
        m_description.setText(ce.getDescription());
        m_date.setText(ce.getDate().toString());
        m_startTime.setText(ce.getStartTime().toString());
        m_endTime.setText(ce.getStartTime().toString());
    }

    public void addEvent(View view){
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2019, 9, 28, 8, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2019, 9, 28, 11, 00);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        startActivity(intent);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Intent redirect = new Intent(this, MainActivity.class);
        startActivity(redirect);

    }

}
