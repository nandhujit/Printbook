package android.app.printerapp.database;

/**
 * Created by Geek on 11/24/2017.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchUser extends  AsyncTask<String, String, JSONArray>{
    String returned [] = new String[3];
    JSONParser jsonParser = new JSONParser();
    String url;
    Config config;
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    public SearchUser(List<NameValuePair> params, String url){
        this.params = params;
        this.url = url;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONArray productObj = null;
        JSONObject json = jsonParser.makeHttpRequest(
                url, "GET", params);
        int success = 0;
        try {
            success = json.getInt(config.TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (success == 1) {
            try {
                productObj = json
                        .getJSONArray("users");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productObj;
    }

}
