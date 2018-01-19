package android.app.printerapp.database;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geek on 11/13/2017.
 */
public class Search extends AsyncTask<String, String, String[]> {
    String returned [] = new String[33];
    JSONParser jsonParser = new JSONParser();
    String url;
    Config config;
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    public Search (List<NameValuePair> params, String url){
        this.params = params;
        this.url = url;
    }
    @Override
    protected String[] doInBackground(String... strings) {
        JSONObject json = jsonParser.makeHttpRequest(
                url, "GET", params);
        Log.d("SEARCH VALUE", "" +json);
        int success = 0;
        try {
            success = json.getInt(config.TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (success == 1) {
            JSONArray productObj = null;
            try {
                productObj = json
                        .getJSONArray("printJob");
                returned = gather_returned_files(productObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            returned[0] = "-101";
        }
        return returned;
    }

    private String [] gather_returned_files(JSONArray productObj) {
        JSONObject product = null;
        try {
            product = productObj.getJSONObject(0);
            returned[0] = product.getString(config.PRINTING_slm_id);
            returned[1] = product.getString(config.PRINTING_start_time);
            Log.d("ONE ONE", returned[1]);
            returned[2] = product.getString(config.PRINTING_end_time);
            returned[3] = product.getString(config.PRINTING_date);
            returned[4] = product.getString(config.PRINTING_operator);
            returned[5] = product.getString(config.PRINTING_machine_type);
            returned[6] = product.getString(config.PRINTING_powder_weight_start);
            returned[7] = product.getString(config.PRINTING_powder_weight_end);
            returned[8] = product.getString(config.PRINTING_powder_waste_weight);
            returned[9] = product.getString(config.PRINTING_powder_used);
            returned[10] = product.getString(config.PRINTING_material_id);
            returned[11] = product.getString(config.PRINTING_build_platform_weight);
            returned[12] = product.getString(config.PRINTING_print_time);
            returned[13] = product.getString(config.PRINTING_powder_condition);
            returned[14] = product.getString(config.PRINTING_reused_times);
            returned[15] = product.getString(config.PRINTING_number_of_layers);
            returned[16] = product.getString(config.PRINTING_dpc_factor);
            returned[17] = product.getString(config.PRINTING_exposure_time);
            returned[18] = product.getString(config.PRINTING_comments);

            //PRE PRINTING
            returned[19] = product.getString(config.PREPRINTING_build_id);
            returned[20] = product.getString(config.PREPRINTING_no_parts);
            returned[21] = product.getString(config.PREPRINTING_printing_parameter);
            returned[22] = product.getString("pre_comments");

            //POST PRINTING
            returned[23] = product.getString(config.POSTPRINTING_support_removal);
            returned[24] = product.getString(config.POSTPRINTING_wedm);
            returned[25] = product.getString(config.POSTPRINTING_wedm_comment);
            returned[26] = product.getString(config.POSTPRINTING_blasting);
            returned[27] = product.getString(config.POSTPRINTING_blasting_time);
            returned[28] = product.getString(config.POSTPRINTING_blasting_type);
            returned[29] = product.getString(config.POSTPRINTING_blasting_comment);

            //PROJECT

            returned[30] = product.getString(config.PREPRINTING_project_id);
            returned[31] = product.getString(config.PRINTING_printing_partnumber);
            returned[32] = product.getString(config.PROJECT_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returned;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }


}