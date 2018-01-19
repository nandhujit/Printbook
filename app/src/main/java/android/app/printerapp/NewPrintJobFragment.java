package android.app.printerapp;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.printerapp.database.CheckRandom;
import android.app.printerapp.database.PathUtil;
import android.app.printerapp.database.Postprint_getIDs;
import android.app.printerapp.database.UploadFilesAsync;
import android.app.printerapp.login.MainActivityNew;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.printerapp.database.Insert;
import android.app.printerapp.database.Config;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPrintJobFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int STL_SELECT_CODE = 0;
    private static final int CAD_SELECT_CODE = 1;
    ProgressDialog progressDialog;
    String ImageName = "slm_id", ImagePath = "image_path" ;
    String SLM_FOUND = "";
    Bitmap bitmap = null;
    String stlpath = "";
    String cadpath = "";
    int success_stl = -1;
    int stress_ids [];
    String returned = "";
    Calendar myCalendar = Calendar.getInstance();
    Config config;
    Insert insert;
    String slm_id;
    Button submit_Button, preprinting_expand_button, printing_expand_button, posprinting_expand_button;
    EditText projectID_editText, partnumber_editText, buildId_editText, numberofparts_editText, printingparameters_editText, comment_editText;
    EditText slmid_editText, starttime_editText, endtime_editText, date_editText, operator_editText, agingComment_editText;
    EditText typeofmachine_editText, powerweight_editText, powerweightatEnd_editText, powderwaste_editText, material_editText;
    EditText buildplatform_editText, printTune_editText, powderused_editText, reused_times_editText, agingNumberofCycles_editText;
    EditText numberofLayers_editText, dpcFactor_editText, minExposureTime_editText, printingComments_editText, hardeningComment_editText;
    EditText WEDMcomments_editText;
    EditText blastingType_editText;
    EditText blastingTime_editText;
    EditText hardeningTime_editText;
    EditText blastingComment_editText, stressTemp_editText, stressTime_editText, stressComment_editText, hardeningTemp_editText;
    EditText temperingTemp_editText, temperingTime_editText, temperingNumberofCycles_editText, temperingComment_editText, solutionTreatmentTemp_editText;
    EditText solutionTreatmentTime_editText, solutionTreatmentComment_editText, agingTemp_editText, agingTime_editText;
    TextView solutionTreatmentTime_Text, solutionTreatmentComment_Text, agingTreatment_Text, agingTemp_Text, agingTime_Text, agingComment_Text;
    TextView temperingTemp_Text, temperingTime_Text, temperingNumberofCycles_Text, temperingComment_Text, solutionTreatment_Text;
    TextView stressComment_Text, hardening_Text, hardeningTemp_Text, hardeningTime_Text, hardeningComment_Text, tempering_Text, agingNumberofCycles_Text;
    TextView blastingComment_Text, heatTreatment_Text, stressRelieving_Text, stressTemp_Text, stressTime_Text, stressShieldingGas_Text;
    TextView supportremoval_Text;
    TextView WEDM_Text;
    TextView WEDMcomments_Text;
    TextView blasting_Text;
    TextView blastingType_Text;
    TextView blastingTime_Text;
    TextView projectID_Text, partnumber_Text, numberofparts_Text, printingparameters_Text, comment_Text, if_textView, solutionTreatmentTemp_Text;
    TextView slmid_Text, starttime_Text, endtime_Text, date_Text, operator_Text, typeofmachine_Text, powerweight_Text;
    TextView powerweightatEnd_Text, powderwaste_Text,material_Text, buildplatform_Text, printTune_Text, powderCondition_Text, numberofLayers_Text;
    TextView dpcFactor_Text, minExposureTime_Text, printingComments_Text, buildId_textView, powderused_textView;
    Spinner spinner, supportremovalSpinner, WEDMSpinner, blastingSpinner, shieldingSpinner;
    TextView projectAcronym_textView, projectAcronym_Edittext;
    ImageView post_print_snapshot;
    Button upload_stl,upload_cad,upload_snapshot;
    EditText recycle_start_edit, recycle_end_edit, right_recycle_start_edit, right_recycle_end_edit;
    TextView recycle_start_left,recycle_end_left,right_recycle_start,right_recycle_end;
    boolean expanded_preprinting = false, expanded_printing = false, expanded_posprinting = false;
    private View newProjectView;
    private View ProgressView;

    public NewPrintJobFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_print_job, container, false);
        slm_id = getArguments().getString("slm_id");
        initialize(view);
        slmid_editText.setText(slm_id);
        slmid_editText.setEnabled(false);
        spinner.setOnItemSelectedListener(this);
        supportremovalSpinner.setOnItemSelectedListener(this);
        WEDMSpinner.setOnItemSelectedListener(this);
        blastingSpinner.setOnItemSelectedListener(this);
        shieldingSpinner.setOnItemSelectedListener(this);
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int success = createProject();
                int success_pre_printing = -1, success_printing = -1, success_postprinting = -1;
                success_pre_printing = insert_to_pre_printing();
                success_printing = insert_to_printing();
                success_postprinting = insert_to_postprinting();
                if(success == 1 & success_postprinting == 1 & success_pre_printing ==1 & success_printing ==1){
                    Toast.makeText(getContext(), "SUCCESSFUL", Toast.LENGTH_LONG).show();
                    if(post_print_snapshot.getDrawable() != null && bitmap != null) ImageUploadToServerFunction();
                }
                else if(success != 1 & success_postprinting != 1 & success_pre_printing !=1 & success_printing !=1){
                    Toast.makeText(getContext(), "Nothing was Successful", Toast.LENGTH_LONG).show();
                }
                else if(success != 1) Toast.makeText(getContext(), "Insertion to Project was Not Successful", Toast.LENGTH_LONG).show();
                else if(success_postprinting != 1) Toast.makeText(getContext(), "Insertion to Post printing was Not Successful", Toast.LENGTH_LONG).show();
                else if(success_pre_printing != 1) Toast.makeText(getContext(), "Insertion to Pre printing was Not Successful", Toast.LENGTH_LONG).show();
                else if(success_printing != 1) Toast.makeText(getContext(), "Insertion to Printing was Not Successful", Toast.LENGTH_LONG).show();
                if(!stlpath.isEmpty()) {
                    int stl_uploaded = upload_stl(".stl", stlpath,config.url_upload_stl);
                    if (stl_uploaded == 1)
                        Toast.makeText(getContext(), "Uploading STL was Successful", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getContext(), "ERROR Uploading STL", Toast.LENGTH_LONG).show();
                }
                if(!cadpath.isEmpty()) {
                    int stl_uploaded = upload_stl(".cad", cadpath,config.url_upload_cad);
                    if (stl_uploaded == 1)
                        Toast.makeText(getContext(), "Uploading CAD was Successful", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getContext(), "ERROR Uploading CAD", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(getContext(), MainActivityNew.class);
                startActivity(intent);
            }
        });
        preprinting_expand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expanded_preprinting) {
                    hide_preprining();
                    expanded_preprinting = false;
                }
                else{
                    show_preprining();
                    expanded_preprinting = true;
                }

            }
        });
        printing_expand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expanded_printing) {
                    hide_prining();
                    expanded_printing = false;
                }
                else{
                    show_prining();
                    expanded_printing = true;
                }

            }
        });

        posprinting_expand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expanded_posprinting) {
                    hide_posprinting();
                    expanded_posprinting = false;
                }
                else{
                    show_posprinting();
                    expanded_posprinting = true;
                }

            }
        });
        date_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth = selectedmonth + 1;
                        date_editText.setText("" + selectedyear + "/" + selectedmonth + "/" + selectedday);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });
        starttime_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        starttime_editText.setText( selectedHour + ":" + selectedMinute);
                        Log.d(TAG, "START TIME 2" + starttime_editText.getText().toString());
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        endtime_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endtime_editText.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        material_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(TextUtils.isEmpty(powerweight_editText.getText().toString()) || TextUtils.isEmpty(powerweightatEnd_editText.getText().toString()) || TextUtils.isEmpty(recycle_start_edit.getText().toString()) || TextUtils.isEmpty(recycle_end_edit.getText().toString()) || TextUtils.isEmpty(right_recycle_end_edit.getText().toString())|| TextUtils.isEmpty(right_recycle_start_edit.getText().toString()) || TextUtils.isEmpty(buildplatform_editText.getText().toString())))
                {
                    int A_B = Integer.parseInt(powerweight_editText.getText().toString()) - Integer.parseInt(powerweightatEnd_editText.getText().toString());
                    int D_C = Integer.parseInt(recycle_end_edit.getText().toString()) - Integer.parseInt(recycle_start_edit.getText().toString());
                    int F_E = Integer.parseInt(right_recycle_end_edit.getText().toString()) - Integer.parseInt(right_recycle_start_edit.getText().toString());
                    int H_G = Integer.parseInt(powderused_editText.getText().toString()) - Integer.parseInt(buildplatform_editText.getText().toString());
                    int _final = A_B - (D_C + F_E) - H_G;
                    powderwaste_editText.setText("" +  _final );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!(TextUtils.isEmpty(powerweight_editText.getText().toString()) || TextUtils.isEmpty(powerweightatEnd_editText.getText().toString()) || TextUtils.isEmpty(recycle_start_edit.getText().toString()) || TextUtils.isEmpty(recycle_end_edit.getText().toString()) || TextUtils.isEmpty(right_recycle_end_edit.getText().toString())|| TextUtils.isEmpty(right_recycle_start_edit.getText().toString()) || TextUtils.isEmpty(buildplatform_editText.getText().toString())))
                {
                    int A_B = Integer.parseInt(powerweight_editText.getText().toString()) - Integer.parseInt(powerweightatEnd_editText.getText().toString());
                    int D_C = Integer.parseInt(recycle_end_edit.getText().toString()) - Integer.parseInt(recycle_start_edit.getText().toString());
                    int F_E = Integer.parseInt(right_recycle_end_edit.getText().toString()) - Integer.parseInt(right_recycle_start_edit.getText().toString());
                    int H_G = Integer.parseInt(powderused_editText.getText().toString()) - Integer.parseInt(buildplatform_editText.getText().toString());
                    int _final = A_B - (D_C + F_E) - H_G;
                    powderwaste_editText.setText("" +  _final );
                }
            }
        });
        upload_stl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStlFileChooser();
            }
        });
        upload_cad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcadFileChooser();
            }
        });
        upload_snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
        return view;

    }

    private int upload_stl(String filetype, String path, String url) {
        int success_upload = -1;
        UploadFilesAsync uploadFilesAsync = new UploadFilesAsync(path, slm_id,filetype,url);
        try {success_upload = uploadFilesAsync.execute(path).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success_upload;
    }

    private int get_post_ids(String url, String name) {
        int random = (int)(Math.random()*1000);
        List <NameValuePair> params = new ArrayList <NameValuePair>();
        params.add(new BasicNameValuePair("ids", "ids"));
        Postprint_getIDs search = new Postprint_getIDs(params, url);
        try {
            JSONArray jarray = search.execute().get();
            stress_ids = new int[jarray.length()];
            Log.d("length", jarray.length() + "");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonObject = jarray.getJSONObject(i);
                String result = jsonObject.getString(name);
                if(result.equals("null")) stress_ids[i] = 0;
                else stress_ids[i] =Integer.parseInt(result);
            }
            CheckRandom checkRandom = new CheckRandom();
            random = checkRandom.check_random(stress_ids,random);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return random;
    }
    private int insert_to_postprinting() {
        String blastingTime = blastingTime_editText.getText().toString();
        if(TextUtils.isEmpty(blastingTime)) blastingTime = "null";
        int success = -1;
        int stress = insert_stress_releving();
        int hard = insert_hardening();
        int temp = insert_tempring();
        int solution = insert_solution();
        int aging = insert_aging();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //AUTO INCREMENT params.add(new BasicNameValuePair(config.PRINTING_printing_id, 2 + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_slm_id, slmid_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_support_removal, supportremovalSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_wedm, WEDMSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_wedm_comment, WEDMcomments_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting, blastingSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_time, blastingTime));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_type, blastingType_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_comment, blastingComment_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_id, aging +""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_solution_id, solution +""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_id, temp +""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_hardening_id, hard +""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_id, stress +""));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING);
        try {
            success = insert.execute().get();
            returned = returned + " BASIC : " + success;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

    private int insert_aging() {
        String temp = agingTemp_editText.getText().toString();
        String time = agingTime_editText.getText().toString();
        String cycles = agingNumberofCycles_editText.getText().toString();
        if(TextUtils.isEmpty(temp)) temp = "null";
        if(TextUtils.isEmpty(time)) time = "null";
        if(TextUtils.isEmpty(cycles)) cycles = "null";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int aging_ids = get_post_ids(config.url_get_aging_ids, "aging_id");
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_id, aging_ids + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_temperature, temp));
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_time, time));
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_cycles, cycles));
        params.add(new BasicNameValuePair(config.POSTPRINTING_aging_comment, agingComment_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING_AGING);
        try {
            insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return aging_ids;
    }

    private int insert_solution() {
        String temp = solutionTreatmentTemp_editText.getText().toString();
        String time = solutionTreatmentTemp_editText.getText().toString();
        if(TextUtils.isEmpty(temp)) temp = "null";
        if(TextUtils.isEmpty(time)) time = "null";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int solution_ids = get_post_ids(config.url_get_solution_ids, "solution_id");
        params.add(new BasicNameValuePair(config.POSTPRINTING_solution_id, solution_ids + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_solution_temperature, temp));
        params.add(new BasicNameValuePair(config.POSTPRINTING_solution_time, time));
        params.add(new BasicNameValuePair(config.POSTPRINTING_solution_comment, solutionTreatmentComment_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING_SOLUTION);
        try {
            insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return solution_ids;
    }

    private int insert_tempring() {
        String temp = temperingTemp_editText.getText().toString();
        String time = temperingTime_editText.getText().toString();
        String cycles = temperingNumberofCycles_editText.getText().toString();
        if(TextUtils.isEmpty(temp)) temp = "null";
        if(TextUtils.isEmpty(time)) time = "null";
        if(TextUtils.isEmpty(cycles)) cycles = "null";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int tempering_ids = get_post_ids(config.url_get_tempering_ids, "tempering_id");
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_id, tempering_ids + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_temperature, temp));
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_time, time));
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_cycles, cycles));
        params.add(new BasicNameValuePair(config.POSTPRINTING_tempering_comment, temperingComment_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING_TEMPERING);
        try {
            insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return tempering_ids;
    }

    private int insert_hardening() {
        String temp = hardeningTemp_editText.getText().toString();
        String time = hardeningTime_editText.getText().toString();
        if(TextUtils.isEmpty(temp)) temp = "null";
        if(TextUtils.isEmpty(time)) time = "null";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int hardening_ids = get_post_ids(config.url_get_hardening_ids, "hardening_id");
        params.add(new BasicNameValuePair(config.POSTPRINTING_hardening_id, hardening_ids + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_hardening_temperature, temp));
        params.add(new BasicNameValuePair(config.POSTPRINTING_hardening_time, time));
        params.add(new BasicNameValuePair(config.POSTPRINTING_hardening_comment, hardeningComment_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING_HARDINING);
        try {
            insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return hardening_ids;
    }

    private int insert_stress_releving() {
        String temp = stressTemp_editText.getText().toString();
        String time = stressTime_editText.getText().toString();
        if(TextUtils.isEmpty(temp)) temp = "null";
        if(TextUtils.isEmpty(time)) time = "null";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int stress_ids = get_post_ids(config.url_get_ids, "stress_id");
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_id, stress_ids + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_temprature, temp));
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_time, time));
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_shielding_gas, shieldingSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_stress_comment, stressComment_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_POSTPRINTING_STRESS);
        try {
            insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return stress_ids;
    }

    private int insert_to_printing() {
        String recycle_startLeft = recycle_start_edit.getText().toString();
        String recycle_endLeft = recycle_end_edit.getText().toString();
        String recycle_startRight = right_recycle_start_edit.getText().toString();
        String recycle_endRight = right_recycle_end_edit.getText().toString();
        String starttime = starttime_editText.getText().toString();
        String endtime = endtime_editText.getText().toString();
        String date = date_editText.getText().toString();
        String powerweight = powerweight_editText.getText().toString();
        String powderatend = powerweightatEnd_editText.getText().toString();
        String powderused= powderused_editText.getText().toString();
        String powderwaste = powderwaste_editText.getText().toString();
        String buildplatform = buildplatform_editText.getText().toString();
        String printTime = printTune_editText.getText().toString();
        String reused_times = reused_times_editText.getText().toString();
        String numberofLayers = numberofLayers_editText.getText().toString();
        String dpcFactor = dpcFactor_editText.getText().toString();
        String minExposureTime = minExposureTime_editText.getText().toString();
        if(TextUtils.isEmpty(starttime)) starttime = "null";
        if(TextUtils.isEmpty(endtime)) endtime = "null";
        if(TextUtils.isEmpty(date)) date = "null";
        if(!(TextUtils.isEmpty(powerweight) || TextUtils.isEmpty(powderatend) || TextUtils.isEmpty(recycle_startLeft) || TextUtils.isEmpty(recycle_endLeft) || TextUtils.isEmpty(recycle_startRight)|| TextUtils.isEmpty(recycle_endRight) || TextUtils.isEmpty(buildplatform)))
        {
            int A_B = Integer.parseInt(powerweight_editText.getText().toString()) - Integer.parseInt(powerweightatEnd_editText.getText().toString());
            int D_C = Integer.parseInt(recycle_end_edit.getText().toString()) - Integer.parseInt(recycle_start_edit.getText().toString());
            int F_E = Integer.parseInt(right_recycle_end_edit.getText().toString()) - Integer.parseInt(right_recycle_start_edit.getText().toString());
            int H_G = Integer.parseInt(powderused_editText.getText().toString()) - Integer.parseInt(buildplatform_editText.getText().toString());
            int _final = A_B - (D_C + F_E) - H_G;
            powderwaste = "" +  _final ;
        }
        if(TextUtils.isEmpty(recycle_startRight))
        {
            recycle_startRight = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(recycle_endRight)){
            recycle_endRight = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(powerweight))
        {
            powerweight = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(powderatend)){
            powderatend = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(recycle_startLeft))
        {
            recycle_startLeft = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(recycle_endLeft)){
            recycle_endLeft = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(buildplatform))
        {
            buildplatform = "null";
            powderwaste = "null";
        }
        if(TextUtils.isEmpty(printTime)) printTime = "null";
        if(TextUtils.isEmpty(reused_times)) reused_times = "null";
        if(TextUtils.isEmpty(numberofLayers)) numberofLayers = "null";
        if(TextUtils.isEmpty(dpcFactor)) dpcFactor = "null";
        if(TextUtils.isEmpty(minExposureTime)) minExposureTime = "null";
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //AUTO INCREMENT params.add(new BasicNameValuePair(config.PRINTING_printing_id, 2 + ""));
        params.add(new BasicNameValuePair(config.PRINTING_slm_id, slmid_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_start_time, starttime));
        params.add(new BasicNameValuePair(config.PRINTING_end_time, endtime));
        params.add(new BasicNameValuePair(config.PRINTING_date, date));
        params.add(new BasicNameValuePair(config.PRINTING_operator, operator_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_machine_type, typeofmachine_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_powder_weight_start, powerweight));
        params.add(new BasicNameValuePair(config.PRINTING_powder_weight_end, powderatend));
        params.add(new BasicNameValuePair(config.PRINTING_powder_used, powderused));

        params.add(new BasicNameValuePair(config.PRINTING_right_recycle_start, recycle_startRight));
        params.add(new BasicNameValuePair(config.PRINTING_right_recycle_end, recycle_endRight));
        params.add(new BasicNameValuePair(config.PRINTING_left_recycle_start, recycle_startLeft));
        params.add(new BasicNameValuePair(config.PRINTING_left_recycle_end, recycle_endLeft));

        params.add(new BasicNameValuePair(config.PRINTING_powder_waste_weight, powderwaste));
        params.add(new BasicNameValuePair(config.PRINTING_material_id, material_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_build_platform_weight, buildplatform));
        params.add(new BasicNameValuePair(config.PRINTING_print_time, printTime));
        params.add(new BasicNameValuePair(config.PRINTING_powder_condition, spinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_reused_times, reused_times));
        params.add(new BasicNameValuePair(config.PRINTING_number_of_layers, numberofLayers));
        params.add(new BasicNameValuePair(config.PRINTING_dpc_factor, dpcFactor));
        params.add(new BasicNameValuePair(config.PRINTING_exposure_time, minExposureTime));
        params.add(new BasicNameValuePair(config.PRINTING_comments, printingComments_editText.getText().toString()));
        insert = new Insert(params, config.TAG_INSERT_PRINTING);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Am here too" + params);
        return success;
    }
    private int createProject() {
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String projectId = projectID_editText.getText().toString();
        String slm_id_project = slmid_editText.getText().toString();
        if(projectId.isEmpty()) projectId = "null";
        params.add(new BasicNameValuePair(config.PROJECT_number, projectId));
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id_project));
        params.add(new BasicNameValuePair(config.PROJECT_name, projectAcronym_Edittext.getText().toString()));
        insert = new Insert(params, config.TAG_CREATE_PROJECT);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;

    }
    private int insert_to_pre_printing(){
        String partnumber = partnumber_editText.getText().toString();
        String slm_id = slmid_editText.getText().toString();
        String buildId = buildId_editText.getText().toString();
        String numberofparts = numberofparts_editText.getText().toString();
        String printingparameters = printingparameters_editText.getText().toString();
        String comment = comment_editText.getText().toString();
        if(TextUtils.isEmpty(buildId)) buildId = "null";
        if(TextUtils.isEmpty(numberofparts)) numberofparts = "null";
        if(TextUtils.isEmpty(printingparameters)) printingparameters = "null";
        if(TextUtils.isEmpty(comment)) comment = "null";
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        int id = (int)(100*Math.random());
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.PREPRINTING_build_id, buildId));
        params.add(new BasicNameValuePair(config.PREPRINTING_no_parts, numberofparts));
        params.add(new BasicNameValuePair(config.PREPRINTING_printing_parameter, printingparameters));
        params.add(new BasicNameValuePair(config.PRINTING_printing_partnumber, "\"" + partnumber + "\""));
        params.add(new BasicNameValuePair(config.PREPRINTING_comment, comment));
        Log.d(TAG, "paramspreprinting" + params);
        insert = new Insert(params, config.TAG_INSERT_PREPRINTING);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }
    private void initialize(View view) {
        recycle_start_edit = (EditText) view.findViewById(R.id.recycle_start_edit);
        recycle_end_edit = (EditText) view.findViewById(R.id.recycle_end_edit);
        right_recycle_start_edit = (EditText) view.findViewById(R.id.right_recycle_start_edit);
        right_recycle_end_edit = (EditText) view.findViewById(R.id.right_recycle_end_edit);

        recycle_start_left = (TextView)view.findViewById(R.id.recycle_start_left);
        recycle_end_left = (TextView)view.findViewById(R.id.recycle_end_left);
        right_recycle_start = (TextView)view.findViewById(R.id.right_recycle_start);
        right_recycle_end = (TextView)view.findViewById(R.id.right_recycle_end);

        ProgressView = view.findViewById(R.id.newprintjob_progress);
        newProjectView = view.findViewById(R.id.uploadfiles_insert);
        spinner = (Spinner) view.findViewById(R.id.powderCondition_editText);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.powder_condition_string, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        supportremovalSpinner = (Spinner) view.findViewById(R.id.supportRemoval_editText);
        ArrayAdapter<CharSequence> supportremovalAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.support_removal_string, R.layout.simple_spinner_item);
        supportremovalAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        supportremovalSpinner.setAdapter(supportremovalAdapter);

        WEDMSpinner = (Spinner) view.findViewById(R.id.WEDM_editText);
        ArrayAdapter<CharSequence> WEDMSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.WEDM_string, R.layout.simple_spinner_item);
        WEDMSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        WEDMSpinner.setAdapter(WEDMSpinnerAdapter);

        blastingSpinner = (Spinner) view.findViewById(R.id.blasting_editText);
        ArrayAdapter<CharSequence> blastingSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blasting_string, R.layout.simple_spinner_item);
        blastingSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        blastingSpinner.setAdapter(blastingSpinnerAdapter);

        shieldingSpinner = (Spinner) view.findViewById(R.id.shielding_editText);
        ArrayAdapter<CharSequence> shieldingSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blasting_string, R.layout.simple_spinner_item);
        shieldingSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        shieldingSpinner.setAdapter(shieldingSpinnerAdapter);

        preprinting_expand_button = (Button)view.findViewById(R.id.preprinting_expand_button);
        projectID_editText = (EditText)view.findViewById(R.id.projectID_editText);
        projectAcronym_textView = (TextView)view.findViewById(R.id.projectAcronym_textView);
        projectAcronym_Edittext = (EditText)view.findViewById(R.id.projectAcronym_Edittext);
        partnumber_editText = (EditText)view.findViewById(R.id.partnumber_editText);
        buildId_editText = (EditText)view.findViewById(R.id.buildId_editText);
        buildId_textView = (TextView) view.findViewById(R.id.buildId_textView);
        numberofparts_editText =  (EditText)view.findViewById(R.id.numberofparts_editText);
        printingparameters_editText = (EditText)view.findViewById(R.id.printingparameters_editText);
        comment_editText = (EditText)view.findViewById(R.id.comment_editText);

        projectID_Text = (TextView) view.findViewById(R.id.projectID_textView);
        partnumber_Text = (TextView)view.findViewById(R.id.partnumber_textView);
        numberofparts_Text =  (TextView)view.findViewById(R.id.numberparts_textView);
        printingparameters_Text = (TextView)view.findViewById(R.id.prinringparameters_textView);
        comment_Text = (TextView)view.findViewById(R.id.coment_textView);

        printing_expand_button = (Button)view.findViewById(R.id.Printing_expand_button);
        slmid_editText = (EditText)view.findViewById(R.id.slmid_editText);
        starttime_editText = (EditText)view.findViewById(R.id.starttime_editText);
        endtime_editText =  (EditText)view.findViewById(R.id.endtime_editText);
        date_editText = (EditText)view.findViewById(R.id.date_editText);
        operator_editText = (EditText)view.findViewById(R.id.search_result_operator_editText);
        typeofmachine_editText = (EditText)view.findViewById(R.id.typeofmachine_editText);
        powerweight_editText = (EditText)view.findViewById(R.id.powderweight_editText);
        powerweightatEnd_editText = (EditText)view.findViewById(R.id.powderweightatEnd_editText);
        powderused_editText = (EditText)view.findViewById(R.id.powderused_editText);
        powderused_textView = (TextView)view.findViewById(R.id.powderused_textView);
        powderwaste_editText = (EditText)view.findViewById(R.id.powderwaste_editText);
        material_editText = (EditText)view.findViewById(R.id.material_editText);
        buildplatform_editText = (EditText)view.findViewById(R.id.buildplatform_editText);
        printTune_editText = (EditText)view.findViewById(R.id.printTune_editText);
        if_textView = (TextView)view.findViewById(R.id. if_textView);
        //powderCondition_editText = (EditText)view.findViewById(R.id.powderCondition_editText);
        reused_times_editText = (EditText)view.findViewById(R.id.if_editText);
        numberofLayers_editText = (EditText)view.findViewById(R.id.numberofLayers_editText);
        dpcFactor_editText = (EditText)view.findViewById(R.id.dpcFactor_editText);
        minExposureTime_editText = (EditText)view.findViewById(R.id.minExposureTime_editText);
        printingComments_editText = (EditText)view.findViewById(R.id.printingComments_editText);

        slmid_Text = (TextView)view.findViewById(R.id.slmid_textView);
        starttime_Text = (TextView)view.findViewById(R.id.starttime_textView);
        endtime_Text =  (TextView)view.findViewById(R.id.endtime_textView);
        date_Text = (TextView)view.findViewById(R.id.date_textView);
        operator_Text = (TextView)view.findViewById(R.id.operator_textView);
        typeofmachine_Text = (TextView)view.findViewById(R.id.typeofmachine_textView);
        powerweight_Text = (TextView)view.findViewById(R.id.powderweight_textView);
        powerweightatEnd_Text = (TextView)view.findViewById(R.id.powderweightatEnd_textView);
        powderwaste_Text = (TextView)view.findViewById(R.id.powderwaste_textView);
        material_Text = (TextView)view.findViewById(R.id.material_textView);
        buildplatform_Text = (TextView)view.findViewById(R.id.buildplatform_textView);
        printTune_Text = (TextView)view.findViewById(R.id.printTune_textView);
        powderCondition_Text = (TextView)view.findViewById(R.id.powderCondition_textView);
        numberofLayers_Text = (TextView)view.findViewById(R.id.numberofLayers_textView);
        dpcFactor_Text = (TextView)view.findViewById(R.id.dpcFactor_textView);
        minExposureTime_Text = (TextView)view.findViewById(R.id.minExposureTime_textView);
        printingComments_Text = (TextView)view.findViewById(R.id.printingComments_textView);

        submit_Button = (Button)view.findViewById(R.id.submit_Button);

        //------ Post-Printing Fields ------//
        posprinting_expand_button = (Button)view.findViewById(R.id.posprinting_expand_button);
        supportremoval_Text = (TextView)view.findViewById(R.id.supportRemoval_textView);
        WEDM_Text = (TextView)view.findViewById(R.id.WEDM_textView);
        WEDMcomments_Text = (TextView)view.findViewById(R.id.WEDMcomments_textView);
        WEDMcomments_editText = (EditText) view.findViewById(R.id.WEDMcomments_editText);
        blasting_Text = (TextView)view.findViewById(R.id.blasting_textView);
        blastingType_Text = (TextView) view.findViewById(R.id.blastingType_textView);
        blastingType_editText = (EditText)view.findViewById(R.id.blastingType_editText);
        blastingTime_Text = (TextView)view.findViewById(R.id.blastingTime_textView);
        blastingTime_editText = (EditText)view.findViewById(R.id.blastingTime_editText);
        blastingComment_Text = (TextView)view.findViewById(R.id.blastingComment_textView);
        blastingComment_editText = (EditText)view.findViewById(R.id.blastingComment_editText);
        heatTreatment_Text = (TextView)view.findViewById(R.id.heatTreatment_textView);
        stressRelieving_Text = (TextView)view.findViewById(R.id.stressRelieving_textView);
        stressTemp_Text = (TextView)view.findViewById(R.id.stressTemp_textView);
        stressTemp_editText = (EditText)view.findViewById(R.id.stressTemp_editText);
        stressTime_Text = (TextView)view.findViewById(R.id.stressTime_textView);
        stressTime_editText = (EditText)view.findViewById(R.id.stressTime_editText);
        stressShieldingGas_Text = (TextView) view.findViewById(R.id.stressShieldingGas_textView);
        stressComment_Text = (TextView) view.findViewById(R.id.stressComment_textView);
        stressComment_editText = (EditText)view.findViewById(R.id.stressComment_editText);
        hardening_Text = (TextView)view.findViewById(R.id.hardening_textView);
        hardeningTemp_Text = (TextView)view.findViewById(R.id.hardeningTemp_textView);
        hardeningTemp_editText = (EditText)view.findViewById(R.id.hardeningTemp_editText);
        hardeningTime_Text = (TextView)view.findViewById(R.id.hardeningTime_textView);
        hardeningTime_editText = (EditText)view.findViewById(R.id.hardeningTime_editText);
        hardeningComment_Text = (TextView)view.findViewById(R.id.hardeningComment_textView);
        hardeningComment_editText = (EditText)view.findViewById(R.id.hardeningComment_editText);
        tempering_Text = (TextView)view.findViewById(R.id.tempering_textView);
        temperingTemp_Text = (TextView)view.findViewById(R.id.temperingTemp_textView);
        temperingTemp_editText = (EditText)view.findViewById(R.id.temperingTemp_editText);
        temperingTime_Text = (TextView)view.findViewById(R.id.temperingTime_textView);
        temperingTime_editText = (EditText)view.findViewById(R.id.temperingTime_editText);
        temperingNumberofCycles_Text = (TextView)view.findViewById(R.id.temperingNumberofCycles_textView);
        temperingNumberofCycles_editText = (EditText)view.findViewById(R.id.temperingNumberofCycles_editText);
        temperingComment_Text = (TextView)view.findViewById(R.id.temperingComment_textView);
        temperingComment_editText = (EditText)view.findViewById(R.id.temperingComment_editText);
        solutionTreatment_Text = (TextView)view.findViewById(R.id.solutionTreatment_textView);
        solutionTreatmentTemp_Text = (TextView)view.findViewById(R.id.solutionTreatmentTemp_textView);
        solutionTreatmentTemp_editText = (EditText)view.findViewById(R.id.solutionTreatmentTemp_editText);
        solutionTreatmentTime_Text = (TextView)view.findViewById(R.id.solutionTreatmentTime_textView);
        solutionTreatmentTime_editText = (EditText)view.findViewById(R.id.solutionTreatmentTime_editText);
        solutionTreatmentComment_Text = (TextView)view.findViewById(R.id.solutionTreatmentComment_textView);
        solutionTreatmentComment_editText = (EditText)view.findViewById(R.id.solutionTreatmentComment_editText);
        agingTreatment_Text = (TextView)view.findViewById(R.id.agingTreatment_textView);
        agingTemp_Text = (TextView)view.findViewById(R.id.agingTemp_textView);
        agingTemp_editText = (EditText)view.findViewById(R.id.agingTemp_editText);
        agingTime_Text = (TextView)view.findViewById(R.id.agingTime_textView);
        agingTime_editText = (EditText)view.findViewById(R.id.agingTime_editText);
        agingNumberofCycles_Text = (TextView)view.findViewById(R.id.agingNumberofCycles_textView);
        agingNumberofCycles_editText = (EditText)view.findViewById(R.id.agingNumberofCycles_editText);
        agingComment_Text = (TextView)view.findViewById(R.id.agingComment_textView);
        agingComment_editText = (EditText)view.findViewById(R.id.agingComment_editText);

        upload_stl = (Button)view.findViewById(R.id.upload_stl);
        upload_cad = (Button)view.findViewById(R.id.upload_cad);
        upload_snapshot = (Button)view.findViewById(R.id.upload_postprinting);
        post_print_snapshot = (ImageView)view.findViewById(R.id.post_print_snapshot);

        hide_preprining();
        hide_prining();
        hide_posprinting();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinners = (Spinner) parent;;
        if(spinners.getId() == R.id.powderCondition_editText) {
            switch (position) {
                case 0:
                    reused_times_editText.setVisibility(View.GONE);
                    if_textView.setVisibility(View.GONE);
                    break;
                case 1:
                    reused_times_editText.setVisibility(View.VISIBLE);
                    if_textView.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    reused_times_editText.setVisibility(View.GONE);
                    if_textView.setVisibility(View.GONE);
                    break;

            }
        }
        else if(spinners.getId() == R.id.supportRemoval_editText) {
        }
        else if(spinners.getId() == R.id.WEDM_editText) {
        }
        else if(spinners.getId() == R.id.blasting_editText) {
        }
        else if(spinners.getId() == R.id.shielding_editText) {
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void hide_preprining(){
        projectAcronym_Edittext.setVisibility(View.GONE);
        projectAcronym_textView.setVisibility(View.GONE);
        projectID_editText.setVisibility(View.GONE);
        partnumber_editText.setVisibility(View.GONE);
        buildId_textView.setVisibility(View.GONE);
        buildId_editText.setVisibility(View.GONE);
        numberofparts_editText.setVisibility(View.GONE);
        printingparameters_editText.setVisibility(View.GONE);
        comment_editText.setVisibility(View.GONE);
        projectID_Text.setVisibility(View.GONE);
        partnumber_Text.setVisibility(View.GONE);
        numberofparts_Text.setVisibility(View.GONE);
        printingparameters_Text.setVisibility(View.GONE);
        comment_Text.setVisibility(View.GONE);
        preprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
    }
    private void show_preprining(){
        projectAcronym_Edittext.setVisibility(View.VISIBLE);
        projectAcronym_textView.setVisibility(View.VISIBLE);
        projectID_editText.setVisibility(View.VISIBLE);
        partnumber_editText.setVisibility(View.VISIBLE);
        buildId_textView.setVisibility(View.VISIBLE);
        buildId_editText.setVisibility(View.VISIBLE);
        numberofparts_editText.setVisibility(View.VISIBLE);
        printingparameters_editText.setVisibility(View.VISIBLE);
        comment_editText.setVisibility(View.VISIBLE);
        preprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shrink, 0);
        projectID_Text.setVisibility(View.VISIBLE);
        partnumber_Text.setVisibility(View.VISIBLE);
        numberofparts_Text.setVisibility(View.VISIBLE);
        printingparameters_Text.setVisibility(View.VISIBLE);
        comment_Text.setVisibility(View.VISIBLE);
    }
    private void hide_prining() {
        recycle_start_left.setVisibility(View.GONE);
        recycle_end_left.setVisibility(View.GONE);
        right_recycle_start.setVisibility(View.GONE);
        right_recycle_end.setVisibility(View.GONE);
        recycle_start_edit.setVisibility(View.GONE);
        recycle_end_edit.setVisibility(View.GONE);
        right_recycle_start_edit.setVisibility(View.GONE);
        right_recycle_end_edit.setVisibility(View.GONE);
        slmid_editText.setVisibility(View.GONE);
        starttime_editText.setVisibility(View.GONE);
        endtime_editText.setVisibility(View.GONE);
        date_editText.setVisibility(View.GONE);
        operator_editText.setVisibility(View.GONE);
        typeofmachine_editText.setVisibility(View.GONE);
        powerweight_editText.setVisibility(View.GONE);
        powerweightatEnd_editText.setVisibility(View.GONE);
        powderused_editText.setVisibility(View.GONE);
        powderused_textView.setVisibility(View.GONE);
        powderwaste_editText.setVisibility(View.GONE);
        material_editText.setVisibility(View.GONE);
        buildplatform_editText.setVisibility(View.GONE);
        printTune_editText.setVisibility(View.GONE);
        if_textView.setVisibility(View.GONE);
        reused_times_editText.setVisibility(View.GONE);
        numberofLayers_editText.setVisibility(View.GONE);
        dpcFactor_editText.setVisibility(View.GONE);
        minExposureTime_editText.setVisibility(View.GONE);
        printingComments_editText.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);

        slmid_Text.setVisibility(View.GONE);
        starttime_Text.setVisibility(View.GONE);
        endtime_Text.setVisibility(View.GONE);
        date_Text.setVisibility(View.GONE);
        operator_Text.setVisibility(View.GONE);
        typeofmachine_Text.setVisibility(View.GONE);
        powerweight_Text.setVisibility(View.GONE);
        powerweightatEnd_Text.setVisibility(View.GONE);
        powderwaste_Text.setVisibility(View.GONE);
        material_Text.setVisibility(View.GONE);
        buildplatform_Text.setVisibility(View.GONE);
        printTune_Text.setVisibility(View.GONE);
        powderCondition_Text.setVisibility(View.GONE);
        numberofLayers_Text.setVisibility(View.GONE);
        dpcFactor_Text.setVisibility(View.GONE);
        minExposureTime_Text.setVisibility(View.GONE);
        printingComments_Text.setVisibility(View.GONE);
        printing_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);

    }
    private void show_prining() {
        recycle_start_left.setVisibility(View.VISIBLE);
        recycle_end_left.setVisibility(View.VISIBLE);
        right_recycle_start.setVisibility(View.VISIBLE);
        right_recycle_end.setVisibility(View.VISIBLE);
        recycle_start_edit.setVisibility(View.VISIBLE);
        recycle_end_edit.setVisibility(View.VISIBLE);
        right_recycle_start_edit.setVisibility(View.VISIBLE);
        right_recycle_end_edit.setVisibility(View.VISIBLE);
        slmid_editText.setVisibility(View.VISIBLE);
        starttime_editText.setVisibility(View.VISIBLE);
        endtime_editText.setVisibility(View.VISIBLE);
        date_editText.setVisibility(View.VISIBLE);
        operator_editText.setVisibility(View.VISIBLE);
        typeofmachine_editText.setVisibility(View.VISIBLE);
        powerweight_editText.setVisibility(View.VISIBLE);
        powderused_editText.setVisibility(View.VISIBLE);
        powderused_textView.setVisibility(View.VISIBLE);
        powerweightatEnd_editText.setVisibility(View.VISIBLE);
        powderwaste_editText.setVisibility(View.VISIBLE);
        material_editText.setVisibility(View.VISIBLE);
        buildplatform_editText.setVisibility(View.VISIBLE);
        printTune_editText.setVisibility(View.VISIBLE);
        numberofLayers_editText.setVisibility(View.VISIBLE);
        dpcFactor_editText.setVisibility(View.VISIBLE);
        minExposureTime_editText.setVisibility(View.VISIBLE);
        printingComments_editText.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        slmid_Text.setVisibility(View.VISIBLE);
        starttime_Text.setVisibility(View.VISIBLE);
        endtime_Text.setVisibility(View.VISIBLE);
        date_Text.setVisibility(View.VISIBLE);
        operator_Text.setVisibility(View.VISIBLE);
        typeofmachine_Text.setVisibility(View.VISIBLE);
        powerweight_Text.setVisibility(View.VISIBLE);
        powerweightatEnd_Text.setVisibility(View.VISIBLE);
        powderwaste_Text.setVisibility(View.VISIBLE);
        material_Text.setVisibility(View.VISIBLE);
        buildplatform_Text.setVisibility(View.VISIBLE);
        printTune_Text.setVisibility(View.VISIBLE);
        powderCondition_Text.setVisibility(View.VISIBLE);
        numberofLayers_Text.setVisibility(View.VISIBLE);
        dpcFactor_Text.setVisibility(View.VISIBLE);
        minExposureTime_Text.setVisibility(View.VISIBLE);
        printingComments_Text.setVisibility(View.VISIBLE);
        printing_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shrink, 0);
    }
    private void hide_posprinting(){
        supportremoval_Text.setVisibility(View.GONE);
        supportremovalSpinner.setVisibility(View.GONE);
        WEDM_Text.setVisibility(View.GONE);
        WEDMSpinner.setVisibility(View.GONE);
        WEDMcomments_Text.setVisibility(View.GONE);
        WEDMcomments_editText.setVisibility(View.GONE);
        blasting_Text.setVisibility(View.GONE);
        blastingSpinner.setVisibility(View.GONE);
        blastingType_Text.setVisibility(View.GONE);
        blastingType_editText.setVisibility(View.GONE);
        blastingTime_Text.setVisibility(View.GONE);
        blastingTime_editText.setVisibility(View.GONE);
        blastingComment_Text.setVisibility(View.GONE);
        blastingComment_editText.setVisibility(View.GONE);
        heatTreatment_Text.setVisibility(View.GONE);
        stressRelieving_Text.setVisibility(View.GONE);
        stressTemp_Text.setVisibility(View.GONE);
        stressTemp_editText.setVisibility(View.GONE);
        stressTime_Text.setVisibility(View.GONE);
        stressTime_editText.setVisibility(View.GONE);
        stressShieldingGas_Text.setVisibility(View.GONE);
        shieldingSpinner.setVisibility(View.GONE);
        stressComment_Text.setVisibility(View.GONE);
        stressComment_editText.setVisibility(View.GONE);
        hardening_Text.setVisibility(View.GONE);
        hardeningTemp_Text.setVisibility(View.GONE);
        hardeningTemp_editText.setVisibility(View.GONE);
        hardeningTime_Text.setVisibility(View.GONE);
        hardeningTime_editText.setVisibility(View.GONE);
        hardeningComment_Text.setVisibility(View.GONE);
        hardeningComment_editText.setVisibility(View.GONE);
        tempering_Text.setVisibility(View.GONE);
        temperingTemp_Text.setVisibility(View.GONE);
        temperingTemp_editText.setVisibility(View.GONE);
        temperingTime_Text.setVisibility(View.GONE);
        temperingTime_editText.setVisibility(View.GONE);
        temperingNumberofCycles_Text.setVisibility(View.GONE);
        temperingNumberofCycles_editText.setVisibility(View.GONE);
        temperingComment_Text.setVisibility(View.GONE);
        temperingComment_editText.setVisibility(View.GONE);
        solutionTreatment_Text.setVisibility(View.GONE);
        solutionTreatmentTemp_Text.setVisibility(View.GONE);
        solutionTreatmentTemp_editText.setVisibility(View.GONE);
        solutionTreatmentTime_Text.setVisibility(View.GONE);
        solutionTreatmentTime_editText.setVisibility(View.GONE);
        solutionTreatmentComment_Text.setVisibility(View.GONE);
        solutionTreatmentComment_editText.setVisibility(View.GONE);
        agingTreatment_Text.setVisibility(View.GONE);
        agingTemp_Text.setVisibility(View.GONE);
        agingTemp_editText.setVisibility(View.GONE);
        agingTime_Text.setVisibility(View.GONE);
        agingTime_editText.setVisibility(View.GONE);
        agingNumberofCycles_Text.setVisibility(View.GONE);
        agingNumberofCycles_editText.setVisibility(View.GONE);
        agingComment_Text.setVisibility(View.GONE);
        agingComment_editText.setVisibility(View.GONE);

        posprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);

    }
    private void show_posprinting(){
        supportremoval_Text.setVisibility(View.VISIBLE);
        supportremovalSpinner.setVisibility(View.VISIBLE);
        WEDM_Text.setVisibility(View.VISIBLE);
        WEDMSpinner.setVisibility(View.VISIBLE);
        WEDMcomments_Text.setVisibility(View.VISIBLE);
        WEDMcomments_editText.setVisibility(View.VISIBLE);
        blasting_Text.setVisibility(View.VISIBLE);
        blastingSpinner.setVisibility(View.VISIBLE);
        blastingType_Text.setVisibility(View.VISIBLE);
        blastingType_editText.setVisibility(View.VISIBLE);
        blastingTime_Text.setVisibility(View.VISIBLE);
        blastingTime_editText.setVisibility(View.VISIBLE);
        blastingComment_Text.setVisibility(View.VISIBLE);
        blastingComment_editText.setVisibility(View.VISIBLE);
        heatTreatment_Text.setVisibility(View.VISIBLE);
        stressRelieving_Text.setVisibility(View.VISIBLE);
        stressTemp_Text.setVisibility(View.VISIBLE);
        stressTemp_editText.setVisibility(View.VISIBLE);
        stressTime_Text.setVisibility(View.VISIBLE);
        stressTime_editText.setVisibility(View.VISIBLE);
        stressShieldingGas_Text.setVisibility(View.VISIBLE);
        shieldingSpinner.setVisibility(View.VISIBLE);
        stressComment_Text.setVisibility(View.VISIBLE);
        stressComment_editText.setVisibility(View.VISIBLE);
        hardening_Text.setVisibility(View.VISIBLE);
        hardeningTemp_Text.setVisibility(View.VISIBLE);
        hardeningTemp_editText.setVisibility(View.VISIBLE);
        hardeningTime_Text.setVisibility(View.VISIBLE);
        hardeningTime_editText.setVisibility(View.VISIBLE);
        hardeningComment_Text.setVisibility(View.VISIBLE);
        hardeningComment_editText.setVisibility(View.VISIBLE);
        tempering_Text.setVisibility(View.VISIBLE);
        temperingTemp_Text.setVisibility(View.VISIBLE);
        temperingTemp_editText.setVisibility(View.VISIBLE);
        temperingTime_Text.setVisibility(View.VISIBLE);
        temperingTime_editText.setVisibility(View.VISIBLE);
        temperingNumberofCycles_Text.setVisibility(View.VISIBLE);
        temperingNumberofCycles_editText.setVisibility(View.VISIBLE);
        temperingComment_Text.setVisibility(View.VISIBLE);
        temperingComment_editText.setVisibility(View.VISIBLE);
        solutionTreatment_Text.setVisibility(View.VISIBLE);
        solutionTreatmentTemp_Text.setVisibility(View.VISIBLE);
        solutionTreatmentTemp_editText.setVisibility(View.VISIBLE);
        solutionTreatmentTime_Text.setVisibility(View.VISIBLE);
        solutionTreatmentTime_editText.setVisibility(View.VISIBLE);
        solutionTreatmentComment_Text.setVisibility(View.VISIBLE);
        solutionTreatmentComment_editText.setVisibility(View.VISIBLE);
        agingTreatment_Text.setVisibility(View.VISIBLE);
        agingTemp_Text.setVisibility(View.VISIBLE);
        agingTemp_editText.setVisibility(View.VISIBLE);
        agingTime_Text.setVisibility(View.VISIBLE);
        agingTime_editText.setVisibility(View.VISIBLE);
        agingNumberofCycles_Text.setVisibility(View.VISIBLE);
        agingNumberofCycles_editText.setVisibility(View.VISIBLE);
        agingComment_Text.setVisibility(View.VISIBLE);
        agingComment_editText.setVisibility(View.VISIBLE);

        posprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shrink, 0);

    }

    private void showStlFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    STL_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void showcadFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    CAD_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case STL_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        stlpath= PathUtil.getPath(getContext(),uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAD_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        cadpath= PathUtil.getPath(getContext(),uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "File Path: " + cadpath);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (checkPermission())
                    {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                        post_print_snapshot.setImageBitmap(bitmap);
                    } else {
                        int x = requestPermission();
                        if (x == 1){
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                            post_print_snapshot.setImageBitmap(bitmap);}
                    }
                }
                else
                {

                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                    post_print_snapshot.setImageBitmap(bitmap);
                }



                Log.d("empty", " " + data);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private int requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.d("fail", "Write External Storage permission allows us to do store images. Please allow this permission in App Settings");
            return 1;
        } else {
            Log.d("true", "Write External Storage permission allows us to do store images. Please allow this permission in App Settings");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return 1;
        }
    }

    private String ImageUploadToServerFunction(){
        Log.d("First","1");

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                Log.d("Second","2");

                progressDialog = ProgressDialog.show(getContext(),"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                showProgress(false);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Log.d("SERVER UPLOAD",string1);

                // Setting image as transparent after done uploading.
                post_print_snapshot.setImageResource(android.R.color.transparent);


            }
            @Override
            protected void onCancelled() {
                showProgress(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                Log.d("Third","3");

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, slmid_editText.getText().toString());

                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(config.ServerUploadPath_postPrinting, HashMapParams);
                Log.d("FirThirdst",FinalData);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        try {
            showProgress(true);
            SLM_FOUND = AsyncTaskUploadClassOBJ.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return SLM_FOUND;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            newProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
            newProjectView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    newProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            ProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            newProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
