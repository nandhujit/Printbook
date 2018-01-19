package android.app.printerapp;

import android.app.Activity;
import android.app.printerapp.database.Config;
import android.app.printerapp.database.Search;
import android.app.printerapp.login.MainActivityNew;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SearchFragmentList extends Fragment  implements View.OnClickListener {
    Config config;
    Button search_by_slm_button_list;
    EditText search_by_slm_EditText_list,search_by_project_EditText_list,search_by_projectacronym_EditText_list;
    TableLayout list_table;
    ScrollView scrollview_table_list;
    String slm_id, project_number, project_acronym;
    View focusView;
    View view;
    String user_name;
    protected FragmentActivity mActivity;

    public SearchFragmentList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null == savedInstanceState){
            Log.d("yyyyyyyyyyy","y");
        }
        SharedPreferences sp1=getContext().getSharedPreferences("Login", MODE_PRIVATE);

         user_name =sp1.getString("user", null);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.search_fragment_list, container, false);
        search_by_slm_EditText_list = (EditText)view.findViewById(R.id.search_by_slm_EditText_list);
        search_by_project_EditText_list = (EditText)view.findViewById(R.id.search_by_project_EditText_list);
        search_by_projectacronym_EditText_list = (EditText)view.findViewById(R.id.search_by_projectacronym_EditText_list);
        search_by_slm_button_list = (Button)view.findViewById(R.id.search_by_slm_button_list);
        list_table = (TableLayout)view.findViewById(R.id.searched_result_list_table);
        scrollview_table_list = (ScrollView)view.findViewById(R.id.scrollview_table_list);
        init();
        search_by_slm_button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollview_table_list.setVisibility(View.GONE);
                slm_id = search_by_slm_EditText_list.getText().toString();
                project_number = search_by_project_EditText_list.getText().toString();
                project_acronym = search_by_projectacronym_EditText_list.getText().toString();
                if (TextUtils.isEmpty(slm_id) && TextUtils.isEmpty(project_number) && TextUtils.isEmpty(project_acronym)) {
                    search_by_slm_EditText_list.setError(getString(R.string.error_field_required));
                    search_by_project_EditText_list.setError(getString(R.string.error_field_required));
                    search_by_projectacronym_EditText_list.setError(getString(R.string.error_field_required));
                    focusView = search_by_slm_EditText_list;
                    focusView.requestFocus();
                }
                else if ((TextUtils.isEmpty(project_acronym) && TextUtils.isEmpty(project_number)) && !TextUtils.isEmpty(slm_id)){
                    search_slm(slm_id);
                }else if ((TextUtils.isEmpty(slm_id) && TextUtils.isEmpty(project_number)) && !TextUtils.isEmpty(project_acronym)){
                    search_project_acronym(project_acronym);
                }else if ((TextUtils.isEmpty(slm_id) && TextUtils.isEmpty(project_acronym)) && !TextUtils.isEmpty(project_number)){
                    search_project_number(project_number);
                }else if (TextUtils.isEmpty(slm_id) &&(!TextUtils.isEmpty(project_acronym) && !TextUtils.isEmpty(project_number))){
                    search_projectNumber_projectAcronym(project_number, project_acronym);
                }else if (TextUtils.isEmpty(project_acronym) &&(!TextUtils.isEmpty(slm_id) && !TextUtils.isEmpty(project_number))){
                    search_slm_projectNumber(slm_id, project_number);
                }else if (TextUtils.isEmpty(project_number) &&(!TextUtils.isEmpty(slm_id) && !TextUtils.isEmpty(project_acronym))){
                    search_slm_projectAcronym(slm_id, project_acronym);
                }else if (!(TextUtils.isEmpty(project_number) && TextUtils.isEmpty(slm_id) && TextUtils.isEmpty(project_acronym))){
                    search_by_all(slm_id, project_acronym,project_number);
                }
            }
        });
        return view;
    }

    public void init(){
        scrollview_table_list.setVisibility(View.GONE);
    }
    private void search_slm(String slm_id) {
        Log.d("am here", "here");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PRINTING_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_search_slm);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_project_number(String project_number) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_number, project_number));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_project_number);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_project_acronym(String project_acronym) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_name, project_acronym));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_project_acronym);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_slm_projectNumber(String slm_id, String project_number) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.PROJECT_number, project_number));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_search_slm_projNumber);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_projectNumber_projectAcronym(String project_number, String project_acronym) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_name, project_acronym));
        params.add(new BasicNameValuePair(config.PROJECT_number, project_number));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_projectAcro_projNumber);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_slm_projectAcronym(String slm_id, String project_acronym) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.PROJECT_name, project_acronym));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_search_slm_projAcro);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void search_by_all(String slm_id, String project_acronym, String project_number) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.PROJECT_name, project_acronym));
        params.add(new BasicNameValuePair(config.PROJECT_number, project_number));
        params.add(new BasicNameValuePair(config.username, user_name));
        SearchList searchlist = new SearchList(params, config.url_search_by_all);
        try {
            JSONArray returned = searchlist.execute().get();
            if(returned==null) Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
            else if(returned.length() > 0) {
                cleanTable(list_table);
                populate_table(returned);
            }
            else Toast.makeText(getContext(), "No VALUE FOUND", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate_table(JSONArray returned) throws JSONException {
        scrollview_table_list.setVisibility(View.VISIBLE);
        TableLayout.LayoutParams tableRowParams= new TableLayout.LayoutParams
                (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        int leftMargin=110;
        int topMargin=5;
        int rightMargin=110;
        int bottomMargin=5;
        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        for (int i = 0; i < returned.length(); i++) {
            JSONObject jsonObject = returned.getJSONObject(i);
            String slmId_value = jsonObject.getString("slm_id");
            String operator_value = jsonObject.getString("operator");
            String date_value = jsonObject.getString("date");
            String proj_name_value = jsonObject.getString("project_name");
            int printing_id = jsonObject.getInt("printing_id");
            TableRow tbrow = new TableRow(getContext());
            TextView slmid = new TextView(getContext());
            slmid.setText(slmId_value);
            if(i%2==0) slmid.setTextColor(Color.WHITE);
            else slmid.setTextColor(Color.GREEN);
            slmid.setGravity(Gravity.CENTER);
            slmid.setTypeface(Typeface.DEFAULT_BOLD);;
            tbrow.addView(slmid);
            TextView date = new TextView(getContext());
            date.setText(date_value);
            if(i%2==0) date.setTextColor(Color.WHITE);
            else date.setTextColor(Color.GREEN);
            date.setGravity(Gravity.CENTER);
            date.setTypeface(Typeface.DEFAULT_BOLD);
            tbrow.addView(date);
            TextView operator = new TextView(getContext());
            operator.setText(operator_value);
            if(i%2==0) operator.setTextColor(Color.WHITE);
            else operator.setTextColor(Color.GREEN);
            operator.setGravity(Gravity.CENTER);
            operator.setTypeface(Typeface.DEFAULT_BOLD);
            tbrow.addView(operator);
            TextView project_name = new TextView(getContext());
            project_name.setText(proj_name_value);
            if(i%2==0) project_name.setTextColor(Color.WHITE);
            else project_name.setTextColor(Color.GREEN);
            project_name.setGravity(Gravity.CENTER);
            project_name.setTypeface(Typeface.DEFAULT_BOLD);
            tbrow.addView(project_name);
            tbrow.setLayoutParams(tableRowParams);
            tbrow.setId(printing_id);
            tbrow.setOnClickListener(this);
            list_table.addView(tbrow);
        }
    }
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    @Override
    public void onClick(View view) {
        int clicked_id = view.getId();
        Log.d("id",clicked_id +"");

        SearchFragment fragment = SearchFragment.newInstance(clicked_id);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
