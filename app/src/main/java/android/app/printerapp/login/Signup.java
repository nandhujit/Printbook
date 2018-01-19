package android.app.printerapp.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.printerapp.login.MainActivityNew;
import android.app.printerapp.R;
import android.app.printerapp.database.Config;
import android.app.printerapp.database.Insert;

import static android.content.ContentValues.TAG;

/**
 * Created by jobaer-pc on 13-Nov-17.
 */

public class Signup extends AppCompatActivity {
    Config config;
    Insert insert;
    EditText edit_first_name, edit_last_name, edit_user_name, edit_password, edit_retype_password;
    Button signUpbutton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final int success = 1;
        setContentView(R.layout.sign_up);
        initialize();

        signUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(edit_first_name.getText().toString().length()==0){
                    edit_first_name.setError("First name not entered");
                    edit_first_name.requestFocus();
                }
                if(edit_last_name.getText().toString().length()==0){
                    edit_last_name.setError("Last name not entered");
                    edit_last_name.requestFocus();
                }
                if(edit_user_name.getText().toString().length()==0){
                    edit_user_name.setError("Username is Required");
                    edit_user_name.requestFocus();
                }
                if(edit_password.getText().toString().length()==0){
                    edit_password.setError("Password not entered");
                    edit_password.requestFocus();
                }
                if(edit_retype_password.getText().toString().length()==0){
                    edit_retype_password.setError("Please confirm password");
                    edit_retype_password.requestFocus();
                }

                if(edit_password.getText().toString().length()>=8 && (isValidPassword(edit_password.getText().toString())) && (edit_password.getText().toString().equals(edit_retype_password.getText().toString()))){
                    if(isNetworkAvailable()){
                        int success = insert_to_sign_up();
                        if(success == 1){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }else Toast.makeText(getApplicationContext(), "CONNECT TO INTERNET", Toast.LENGTH_LONG).show();

                }

                else {
                    edit_password.setError("Password is Not Valid");
                    edit_password.requestFocus();
                }

            }

        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void initialize() {
        edit_first_name =(EditText)findViewById(R.id.your_first_name);
        edit_last_name =(EditText)findViewById(R.id.your_last_name);
        edit_user_name = (EditText)findViewById(R.id.your_user_name);
        edit_password = (EditText)findViewById(R.id.create_new_password);
        edit_retype_password = (EditText)findViewById(R.id.new_retype_password);
        signUpbutton = (Button)findViewById(R.id.sign_up_button);
    }
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
//      final String PASSWORD_PATTERN = "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}";
        final String PASSWORD_PATTERN = "[a-z0-9]{8,24}";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private int insert_to_sign_up(){
        int success = 1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //AUTO INCREMENT params.add(new BasicNameValuePair(config.PRINTING_printing_id, 2 + ""));
        params.add(new BasicNameValuePair(config.SIGNUP_first_name, edit_first_name.getText().toString()));
        params.add(new BasicNameValuePair(config.SIGNUP_last_name, edit_last_name.getText().toString()));
        params.add(new BasicNameValuePair(config.SIGNUP_user_name, edit_user_name.getText().toString()));
        params.add(new BasicNameValuePair(config.SIGNUP_email_address, ""));
        params.add(new BasicNameValuePair(config.SIGNUP_password_name, edit_password.getText().toString()));
        params.add(new BasicNameValuePair(config.SIGNUP_retype_password_name, edit_retype_password.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_SIGNUP);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "signup signup" + params);
        successful_login();
        return success;

    }
    private void successful_login() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}