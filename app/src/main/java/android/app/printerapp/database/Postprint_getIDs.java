package android.app.printerapp.database;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geek on 11/27/2017.
 */

public class Postprint_getIDs extends AsyncTask<String, String, JSONArray> {
    JSONParser jsonParser = new JSONParser();
    String url;
    Config config;
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    JSONArray arrayj = null;
    public Postprint_getIDs (List<NameValuePair> params, String url){
        this.params = params;
        this.url = url;
    }
    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONObject json = jsonParser.makeHttpRequest(
                url, "GET", params);
        Log.d("here1" , "" + json);
        try {
            json.getInt(config.TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
             arrayj = json.getJSONArray("ids");
             Log.d("here2" , "" + arrayj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayj;
    }

}
