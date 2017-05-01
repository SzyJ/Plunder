package team18.com.plunder.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by guillermochibas on 01/05/2017.
 */

public class Event implements Serializable{
    private String name;
    private float startLat;
    private float startLng;
    private long startDate;
    private String huntId;
    private String description;
    private String prize;
    private String password;
    private String userId;

    private final static String GET_EVENT_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/get_event.php";

    public Event(final String name) {
        this.name = name;
    }

    public void fillEvent(final String eventId){
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("event_id", eventId)
                        .build();
                Request request = new Request.Builder()
                        .url(GET_EVENT_URL)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    JSONObject object = new JSONObject(response.body().string());
                    name = object.getString("event_name");
                    startLat = Float.valueOf(object.getString("start_lat"));
                    startLng = Float.valueOf(object.getString("start_lng"));
                    startDate = Long.parseLong(object.getString("start_date"));
                    huntId = object.getString("hunt_id");
                    description = object.getString("description");
                    prize = object.getString("prize");
                    userId = object.getString("user_id");
                    password = object.getString("password");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };
        task.execute();
    }


    public float getStartLat() {
        return startLat;
    }
    public String getName() {
        return name;
    }
    public long getStartDate() {
        return startDate;
    }
    public float getStartLng() {
        return startLng;
    }
    public String getHuntId() {
        return huntId;
    }
    public String getDescription() {
        return description;
    }
    public String getPrize() {
        return prize;
    }
    public String getPassword() {
        return password;
    }
    public String getUserId(){
        return userId;
    }

}
