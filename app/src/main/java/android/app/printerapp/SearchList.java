package android.app.printerapp;

import android.app.printerapp.database.Config;
import android.app.printerapp.database.JSONParser;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geek on 12/2/2017.
 */

public class SearchList extends AsyncTask<String, JSONArray, JSONArray> {
    String url;
    Config config;
    JSONParser jsonParser = new JSONParser();
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    public SearchList(List<NameValuePair> params, String url){
        this.params = params;
        this.url = url;
    }
    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONArray productObj = null;
        JSONObject json = jsonParser.makeHttpRequest(
                url, "POST", params);
        int success = 0;
        try {
            success = json.getInt(config.TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (success == 1) {
            try {
                productObj = json
                        .getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productObj;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
    }
}
