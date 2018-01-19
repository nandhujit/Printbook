package android.app.printerapp;

import android.app.ProgressDialog;
import android.app.printerapp.database.FetchMagic;
import android.app.printerapp.database.Insert;
import android.app.printerapp.login.Login;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.printerapp.database.Config;
import android.app.printerapp.database.Search;
import android.app.printerapp.database.CheckIfFileExists;

import static android.content.ContentValues.TAG;
import static android.os.Environment.getDataDirectory;

public class SearchFragment extends Fragment {
    String url_server_stl = "https://group5sep.000webhostapp.com/stl/";
    String url_server_magic = "https://group5sep.000webhostapp.com/magic_files/";
    String url_server_postprint = "https://group5sep.000webhostapp.com/post_printing/";
    //String url_server_stl = "http://10.0.2.2:8081/PrinterBook/stl/";
    //String url_server_magic = "http://10.0.2.2:8081/PrinterBook/magic_files/";
    //String url_server_postprint = "http://10.0.2.2:8081/PrinterBook/post_printing/";
    ProgressDialog mProgressDialog;
    String returned [] = new String[33];
    View view;
    String submitted_slm_id;
    Config config;
    Search search;
    Button  submit_Button, preprinting_expand_button, printing_expand_button, posprinting_expand_button;
    EditText projectID_editText, partnumber_editText, numberofparts_editText, printingparameters_editText, comment_editText;
    EditText starttime_editText, endtime_editText, date_editText, operator_editText, agingComment_editText;
    EditText typeofmachine_editText, powerweight_editText, powerweightatEnd_editText, powderwaste_editText, material_editText;
    EditText buildplatform_editText, printTune_editText, powderCondition_editText, reused_times_editText, agingNumberofCycles_editText;
    EditText numberofLayers_editText, dpcFactor_editText, minExposureTime_editText, printingComments_editText, hardeningComment_editText;
    EditText postID_editText, urlphoto_editText, WEDMcomments_editText, blastingType_editText, blastingTime_editText, hardeningTime_editText;
    EditText blastingComment_editText, stressTemp_editText, stressTime_editText, stressComment_editText, hardeningTemp_editText;
    EditText temperingTemp_editText, temperingTime_editText, temperingNumberofCycles_editText, temperingComment_editText, solutionTreatmentTemp_editText;
    EditText solutionTreatmentTime_editText, solutionTreatmentComment_editText, agingTemp_editText, agingTime_editText;
    TextView solutionTreatmentTime_Text, solutionTreatmentComment_Text, agingTreatment_Text, agingTemp_Text, agingTime_Text, agingComment_Text;
    TextView temperingTemp_Text, temperingTime_Text, temperingNumberofCycles_Text, temperingComment_Text, solutionTreatment_Text;
    TextView stressComment_Text, hardening_Text, hardeningTemp_Text, hardeningTime_Text, hardeningComment_Text, tempering_Text, agingNumberofCycles_Text;
    TextView blastingComment_Text, heatTreatment_Text, stressRelieving_Text, stressTemp_Text, stressTime_Text, stressShieldingGas_Text;
    TextView postID_Text, urlphoto_Text, supportremoval_Text, WEDM_Text, WEDMcomments_Text, blasting_Text, blastingType_Text, blastingTime_Text;
    TextView projectID_Text, partnumber_Text, numberofparts_Text, printingparameters_Text, comment_Text, if_textView, solutionTreatmentTemp_Text;
    TextView starttime_Text, endtime_Text, date_Text, operator_Text, typeofmachine_Text, powerweight_Text;
    TextView powerweightatEnd_Text, powderwaste_Text,material_Text, buildplatform_Text, printTune_Text, powderCondition_Text, numberofLayers_Text;
    TextView dpcFactor_Text, minExposureTime_Text, printingComments_Text, magic_file_text_description;
    Spinner spinner, supportremovalSpinner, WEDMSpinner, blastingSpinner, shieldingSpinner;
    EditText searched_result_projectAcronym_editText, searched_result_buildId_editText;
    TextView searched_result_projectAcronym_textView, searched_result_buildId_textView, postprinting_file_text_description;
    boolean expanded_preprinting = false, expanded_printing = false, expanded_posprinting = false;
    Button edit_key;
    boolean edit_key_pressed = false;
    private static final String ARG_PARAM1 = "param1";

    private int printing_id;
    private ImageView imageview, image_preview_post_printing;
    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance(int printing_id) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, printing_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            printing_id = getArguments().getInt(ARG_PARAM1);
        }
    }

    public void start(){
        reset();
        hide_all();
        search_by_slm(printing_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.search_fragment, container, false);
        initialize();
        initialize_returned();
        hide_all();
        edit_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_key_pressed) {
                    make_editable_uneditable(!edit_key_pressed);
                    edit_key_pressed = false;
                }
                else {
                    make_editable_uneditable(!edit_key_pressed);
                    edit_key_pressed = true;
                }
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
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckIfFileExists checkIfFileExists = new CheckIfFileExists();
                String searchPath = url_server_stl + submitted_slm_id + ".stl";
                try {
                    if(checkIfFileExists.execute(searchPath).get()){
                        mProgressDialog = new ProgressDialog(getContext());
                        mProgressDialog.setMessage("A message");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setCancelable(true);
                        final SearchFragment.DownloadFile downloadTask = new SearchFragment.DownloadFile(getContext());
                        String pathNew = "";
                        try {
                            pathNew = downloadTask.execute(searchPath, submitted_slm_id + ".stl").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        Intent mainIntent = new Intent().setClass(getContext(), MainActivity.class);
                        mainIntent.putExtra("path",pathNew);
                        startActivity(mainIntent);

                    }
                    else{
                        Toast.makeText(getContext(),"NO STL FOUND",Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"ERROR IN DOWNLOADING STL ",Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"ERROR IN DOWNLOADING STL ",Toast.LENGTH_LONG).show();
                }
            }
        });
        start();
        submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_preprinting();
                update_printing();
                update_project();
                update_postprinting();
                make_editable_uneditable(false);
            }
        });
        return view;
    }

    private void search_by_slm(int slm_id) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PRINTING_slm_id, slm_id +""));
        search = new Search(params, config.url_search_by_prininId);
        try {
            returned = search.execute().get();
            if(returned[0] == "-101"){
                Toast.makeText(view.getContext(),"No SLM FOUND", Toast.LENGTH_LONG).show();
            }
            else {
                submitted_slm_id = returned[0];
                show_all();
                make_editable_uneditable(false);
                search_Result(returned);
                show_preview();
                show_preview_postprinting();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search_Result(String returned []) {
        search_result_printing(returned);
        search_result_PostPrinting(returned);
        search_result_PrePrinting(returned);
    }

    private void search_result_printing(String returned []){
        starttime_editText.setText(returned[1]);
        endtime_editText.setText(returned[2]);
        date_editText.setText(returned[3]);
        operator_editText.setText(returned[4]);
        typeofmachine_editText.setText(returned[5]);
        powerweight_editText.setText(returned[6]);
        powerweightatEnd_editText.setText(returned[7]);
        powderwaste_editText.setText(returned[8]);
        material_editText.setText(returned[10]);
        buildplatform_editText.setText(returned[11]);
        printTune_editText.setText(returned[12]);
        reused_times_editText.setText(returned[14]);
        numberofLayers_editText.setText(returned[15]);
        dpcFactor_editText.setText(returned[16]);
        minExposureTime_editText.setText(returned[17]);
        printingComments_editText.setText(returned[18]);
    }
    private void search_result_PrePrinting(String returned []){
        projectID_editText.setText(returned[30]);
        partnumber_editText.setText(returned[31]);
        numberofparts_editText.setText(returned[20]);
        printingparameters_editText.setText(returned[21]);
        comment_editText.setText(returned[22]);
        searched_result_buildId_editText.setText(returned[19]);
        searched_result_projectAcronym_editText.setText(returned[32]);
    }
    private void reset_PrePrinting(){
        String reset = null;
        projectID_editText.setText(reset);
        partnumber_editText.setText(reset);
        numberofparts_editText.setText(reset);
        printingparameters_editText.setText(reset);
        comment_editText.setText(reset);
        searched_result_buildId_editText.setText(reset);
        searched_result_projectAcronym_editText.setText(reset);
    }
    private void search_result_PostPrinting(String returned []){
        int selectionId_WEDM = -1, selectionId_Support = -1, selectionId_blasting = -1;
        if(returned[23].equals("Yes"))selectionId_Support = 1;
        else if(returned[23].equals("No"))selectionId_Support = 2;
        else selectionId_Support = 0;
        if(returned[24].equals("Yes"))selectionId_WEDM = 1;
        else if(returned[24].equals("No"))selectionId_WEDM = 2;
        else selectionId_WEDM = 0;
        if(returned[26].equals("Yes"))selectionId_blasting = 1;
        else if(returned[26].equals("No"))selectionId_blasting = 2;
        else selectionId_blasting = 0;
        supportremovalSpinner.setSelection(selectionId_Support);
        WEDMSpinner.setSelection(selectionId_WEDM);
        WEDMcomments_editText.setText(returned[25]);
        blastingSpinner.setSelection(selectionId_blasting);
        blastingType_editText.setText(returned[28]);
        blastingTime_editText.setText(returned[27]);
        blastingComment_editText.setText(returned[29]);
        /*stressTemp_editText.setVisibility(View.GONE);
        stressTime_editText.setVisibility(View.GONE);
        shieldingSpinner.setVisibility(View.GONE);
        stressComment_editText.setVisibility(View.GONE);
        hardeningTemp_editText.setVisibility(View.GONE);
        hardeningTime_editText.setVisibility(View.GONE);
        hardeningComment_editText.setVisibility(View.GONE);
        temperingTemp_editText.setVisibility(View.GONE);
        temperingTime_editText.setVisibility(View.GONE);
        temperingNumberofCycles_editText.setVisibility(View.GONE);
        temperingComment_editText.setVisibility(View.GONE);
        solutionTreatmentTemp_editText.setVisibility(View.GONE);
        solutionTreatmentTime_editText.setVisibility(View.GONE);
        solutionTreatmentComment_editText.setVisibility(View.GONE);
        agingTemp_editText.setVisibility(View.GONE);
        agingNumberofCycles_editText.setVisibility(View.GONE);
        agingComment_editText.setVisibility(View.GONE);*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initialize(){
        edit_key = (Button) view.findViewById(R.id.edit_key);
    }
    private void initialize_returned() {
        magic_file_text_description = (TextView)view.findViewById(R.id.magic_file_text_description);
        spinner = (Spinner) view.findViewById(R.id.searched_result_powderCondition_editText);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.powder_condition_string, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        supportremovalSpinner = (Spinner) view.findViewById(R.id.searched_result_supportRemoval_editText);
        ArrayAdapter<CharSequence> supportremovalAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.support_removal_string, R.layout.simple_spinner_item);
        supportremovalAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        supportremovalSpinner.setAdapter(supportremovalAdapter);

        WEDMSpinner = (Spinner) view.findViewById(R.id.searched_result_WEDM_editText);
        ArrayAdapter<CharSequence> WEDMSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.WEDM_string, R.layout.simple_spinner_item);
        WEDMSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        WEDMSpinner.setAdapter(WEDMSpinnerAdapter);

        blastingSpinner = (Spinner) view.findViewById(R.id.searched_result_blasting_editText);
        ArrayAdapter<CharSequence> blastingSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blasting_string, R.layout.simple_spinner_item);
        blastingSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        blastingSpinner.setAdapter(blastingSpinnerAdapter);

        shieldingSpinner = (Spinner) view.findViewById(R.id.searched_result_shielding_editText);
        ArrayAdapter<CharSequence> shieldingSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blasting_string, R.layout.simple_spinner_item);
        shieldingSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        shieldingSpinner.setAdapter(shieldingSpinnerAdapter);

        preprinting_expand_button = (Button)view.findViewById(R.id.searched_result_preprinting_expand_button);
        projectID_editText = (EditText)view.findViewById(R.id.searched_result_projectID_editText);
        partnumber_editText = (EditText)view.findViewById(R.id.searched_result_partnumber_editText);
        numberofparts_editText =  (EditText)view.findViewById(R.id.searched_result_numberofparts_editText);
        printingparameters_editText = (EditText)view.findViewById(R.id.searched_result_printingparameters_editText);
        comment_editText = (EditText)view.findViewById(R.id.searched_result_comment_editText);
        searched_result_projectAcronym_editText = (EditText)view.findViewById(R.id.searched_result_projectAcronym_editText);
        searched_result_buildId_editText = (EditText)view.findViewById(R.id.searched_result_buildId_editText);
        searched_result_projectAcronym_textView = (TextView) view.findViewById(R.id.searched_result_projectAcronym_textView);
        searched_result_buildId_textView = (TextView) view.findViewById(R.id.searched_result_buildId_textView);

        projectID_Text = (TextView) view.findViewById(R.id.searched_result_projectID_textView);
        partnumber_Text = (TextView)view.findViewById(R.id.searched_result_partnumber_textView);
        numberofparts_Text =  (TextView)view.findViewById(R.id.searched_result_numberparts_textView);
        printingparameters_Text = (TextView)view.findViewById(R.id.searched_result_prinringparameters_textView);
        comment_Text = (TextView)view.findViewById(R.id.searched_result_coment_textView);

        printing_expand_button = (Button)view.findViewById(R.id.searched_result_Printing_expand_button);
        starttime_editText = (EditText)view.findViewById(R.id.searched_result_starttime_editText);
        endtime_editText =  (EditText)view.findViewById(R.id.searched_result_endtime_editText);
        date_editText = (EditText)view.findViewById(R.id.searched_result_date_editText);
        operator_editText = (EditText)view.findViewById(R.id.searched_result_operator_editText);
        typeofmachine_editText = (EditText)view.findViewById(R.id.searched_result_typeofmachine_editText);
        powerweight_editText = (EditText)view.findViewById(R.id.searched_result_powderweight_editText);
        powerweightatEnd_editText = (EditText)view.findViewById(R.id.searched_result_powderweightatEnd_editText);
        powderwaste_editText = (EditText)view.findViewById(R.id.searched_result_powderwaste_editText);
        material_editText = (EditText)view.findViewById(R.id.searched_result_material_editText);
        buildplatform_editText = (EditText)view.findViewById(R.id.searched_result_buildplatform_editText);
        printTune_editText = (EditText)view.findViewById(R.id.searched_result_printTune_editText);
        if_textView = (TextView)view.findViewById(R.id.searched_result_if_textView);
        reused_times_editText = (EditText)view.findViewById(R.id.searched_result_if_editText);
        numberofLayers_editText = (EditText)view.findViewById(R.id.searched_result_numberofLayers_editText);
        dpcFactor_editText = (EditText)view.findViewById(R.id.searched_result_dpcFactor_editText);
        minExposureTime_editText = (EditText)view.findViewById(R.id.searched_result_minExposureTime_editText);
        printingComments_editText = (EditText)view.findViewById(R.id.searched_result_printingComments_editText);

        starttime_Text = (TextView)view.findViewById(R.id.searched_result_starttime_textView);
        endtime_Text =  (TextView)view.findViewById(R.id.searched_result_endtime_textView);
        date_Text = (TextView)view.findViewById(R.id.searched_result_date_textView);
        operator_Text = (TextView)view.findViewById(R.id.searched_result_operator_textView);
        typeofmachine_Text = (TextView)view.findViewById(R.id.searched_result_typeofmachine_textView);
        powerweight_Text = (TextView)view.findViewById(R.id.searched_result_powderweight_textView);
        powerweightatEnd_Text = (TextView)view.findViewById(R.id.searched_result_powderweightatEnd_textView);
        powderwaste_Text = (TextView)view.findViewById(R.id.searched_result_powderwaste_textView);
        material_Text = (TextView)view.findViewById(R.id.searched_result_material_textView);
        buildplatform_Text = (TextView)view.findViewById(R.id.searched_result_buildplatform_textView);
        printTune_Text = (TextView)view.findViewById(R.id.searched_result_printTune_textView);
        powderCondition_Text = (TextView)view.findViewById(R.id.searched_result_powderCondition_textView);
        numberofLayers_Text = (TextView)view.findViewById(R.id.searched_result_numberofLayers_textView);
        dpcFactor_Text = (TextView)view.findViewById(R.id.searched_result_dpcFactor_textView);
        minExposureTime_Text = (TextView)view.findViewById(R.id.searched_result_minExposureTime_textView);
        printingComments_Text = (TextView)view.findViewById(R.id.searched_result_printingComments_textView);

        submit_Button = (Button)view.findViewById(R.id.searched_result_submit_Button);

        //------ Post-Printing Fields ------//
        posprinting_expand_button = (Button)view.findViewById(R.id.searched_result_posprinting_expand_button);
        postID_Text = (TextView)view.findViewById(R.id.searched_result_postID_textView);
        postID_editText = (EditText)view.findViewById(R.id.searched_result_postID_editText);
        urlphoto_Text = (TextView)view.findViewById(R.id.searched_result_urlphoto_textView);
        urlphoto_editText = (EditText)view.findViewById(R.id.searched_result_urlphoto_editText);
        supportremoval_Text = (TextView)view.findViewById(R.id.searched_result_supportRemoval_textView);
        WEDM_Text = (TextView)view.findViewById(R.id.searched_result_WEDM_textView);
        WEDMcomments_Text = (TextView)view.findViewById(R.id.searched_result_WEDMcomments_textView);
        WEDMcomments_editText = (EditText) view.findViewById(R.id.searched_result_WEDMcomments_editText);
        blasting_Text = (TextView)view.findViewById(R.id.searched_result_blasting_textView);
        blastingType_Text = (TextView) view.findViewById(R.id.searched_result_blastingType_textView);
        blastingType_editText = (EditText)view.findViewById(R.id.searched_result_blastingType_editText);
        blastingTime_Text = (TextView)view.findViewById(R.id.searched_result_blastingTime_textView);
        blastingTime_editText = (EditText)view.findViewById(R.id.searched_result_blastingTime_editText);
        blastingComment_Text = (TextView)view.findViewById(R.id.searched_result_blastingComment_textView);
        blastingComment_editText = (EditText)view.findViewById(R.id.searched_result_blastingComment_editText);
        heatTreatment_Text = (TextView)view.findViewById(R.id.searched_result_heatTreatment_textView);
        stressRelieving_Text = (TextView)view.findViewById(R.id.searched_result_stressRelieving_textView);
        stressTemp_Text = (TextView)view.findViewById(R.id.searched_result_stressTemp_textView);
        stressTemp_editText = (EditText)view.findViewById(R.id.searched_result_stressTemp_editText);
        stressTime_Text = (TextView)view.findViewById(R.id.searched_result_stressTime_textView);
        stressTime_editText = (EditText)view.findViewById(R.id.searched_result_stressTime_editText);
        stressShieldingGas_Text = (TextView) view.findViewById(R.id.searched_result_stressShieldingGas_textView);
        stressComment_Text = (TextView) view.findViewById(R.id.searched_result_stressComment_textView);
        stressComment_editText = (EditText)view.findViewById(R.id.searched_result_stressComment_editText);
        hardening_Text = (TextView)view.findViewById(R.id.searched_result_hardening_textView);
        hardeningTemp_Text = (TextView)view.findViewById(R.id.searched_result_hardeningTemp_textView);
        hardeningTemp_editText = (EditText)view.findViewById(R.id.searched_result_hardeningTemp_editText);
        hardeningTime_Text = (TextView)view.findViewById(R.id.searched_result_hardeningTime_textView);
        hardeningTime_editText = (EditText)view.findViewById(R.id.searched_result_hardeningTime_editText);
        hardeningComment_Text = (TextView)view.findViewById(R.id.searched_result_hardeningComment_textView);
        hardeningComment_editText = (EditText)view.findViewById(R.id.searched_result_hardeningComment_editText);
        tempering_Text = (TextView)view.findViewById(R.id.searched_result_tempering_textView);
        temperingTemp_Text = (TextView)view.findViewById(R.id.searched_result_temperingTemp_textView);
        temperingTemp_editText = (EditText)view.findViewById(R.id.searched_result_temperingTemp_editText);
        temperingTime_Text = (TextView)view.findViewById(R.id.searched_result_temperingTime_textView);
        temperingTime_editText = (EditText)view.findViewById(R.id.searched_result_temperingTime_editText);
        temperingNumberofCycles_Text = (TextView)view.findViewById(R.id.searched_result_temperingNumberofCycles_textView);
        temperingNumberofCycles_editText = (EditText)view.findViewById(R.id.searched_result_temperingNumberofCycles_editText);
        temperingComment_Text = (TextView)view.findViewById(R.id.searched_result_temperingComment_textView);
        temperingComment_editText = (EditText)view.findViewById(R.id.searched_result_temperingComment_editText);
        solutionTreatment_Text = (TextView)view.findViewById(R.id.searched_result_solutionTreatment_textView);
        solutionTreatmentTemp_Text = (TextView)view.findViewById(R.id.searched_result_solutionTreatmentTemp_textView);
        solutionTreatmentTemp_editText = (EditText)view.findViewById(R.id.searched_result_solutionTreatmentTemp_editText);
        solutionTreatmentTime_Text = (TextView)view.findViewById(R.id.searched_result_solutionTreatmentTime_textView);
        solutionTreatmentTime_editText = (EditText)view.findViewById(R.id.searched_result_solutionTreatmentTime_editText);
        solutionTreatmentComment_Text = (TextView)view.findViewById(R.id.searched_result_solutionTreatmentComment_textView);
        solutionTreatmentComment_editText = (EditText)view.findViewById(R.id.searched_result_solutionTreatmentComment_editText);
        agingTreatment_Text = (TextView)view.findViewById(R.id.searched_result_agingTreatment_textView);
        agingTemp_Text = (TextView)view.findViewById(R.id.searched_result_agingTemp_textView);
        agingTemp_editText = (EditText)view.findViewById(R.id.searched_result_agingTemp_editText);
        agingTime_Text = (TextView)view.findViewById(R.id.searched_result_agingTime_textView);
        agingTime_editText = (EditText)view.findViewById(R.id.searched_result_agingTime_editText);
        agingNumberofCycles_Text = (TextView)view.findViewById(R.id.searched_result_agingNumberofCycles_textView);
        agingNumberofCycles_editText = (EditText)view.findViewById(R.id.searched_result_agingNumberofCycles_editText);
        agingComment_Text = (TextView)view.findViewById(R.id.searched_result_agingComment_textView);
        agingComment_editText = (EditText)view.findViewById(R.id.searched_result_agingComment_editText);

        imageview = (ImageView)view.findViewById(R.id.image_preview);
        image_preview_post_printing = (ImageView)view.findViewById(R.id.image_preview_post_printing);
        postprinting_file_text_description = (TextView)view.findViewById(R.id.postprinting_file_text_description);
    }
    private void hide_all(){
        magic_file_text_description.setVisibility(View.GONE);
        edit_key.setVisibility(View.GONE);
        posprinting_expand_button.setVisibility(View.GONE);
        preprinting_expand_button.setVisibility(View.GONE);
        printing_expand_button.setVisibility(View.GONE);
        submit_Button.setVisibility(View.GONE);
        imageview.setVisibility(View.GONE);
        image_preview_post_printing.setVisibility(View.GONE);
        postprinting_file_text_description.setVisibility(View.GONE);
        hide_preprining();
        hide_prining();
        hide_posprinting();
    }
    private void show_all(){
        magic_file_text_description.setVisibility(View.VISIBLE);
        edit_key.setVisibility(View.VISIBLE);
        posprinting_expand_button.setVisibility(View.VISIBLE);
        preprinting_expand_button.setVisibility(View.VISIBLE);
        printing_expand_button.setVisibility(View.VISIBLE);
        imageview.setVisibility(View.VISIBLE);
        image_preview_post_printing.setVisibility(View.VISIBLE);
        postprinting_file_text_description.setVisibility(View.VISIBLE);
    }
    private void hide_preprining(){
        projectID_editText.setVisibility(View.GONE);
        partnumber_editText.setVisibility(View.GONE);
        numberofparts_editText.setVisibility(View.GONE);
        printingparameters_editText.setVisibility(View.GONE);
        comment_editText.setVisibility(View.GONE);
        projectID_Text.setVisibility(View.GONE);
        partnumber_Text.setVisibility(View.GONE);
        numberofparts_Text.setVisibility(View.GONE);
        printingparameters_Text.setVisibility(View.GONE);
        comment_Text.setVisibility(View.GONE);
        searched_result_projectAcronym_editText.setVisibility(View.GONE);
        searched_result_buildId_editText.setVisibility(View.GONE);
        searched_result_projectAcronym_textView.setVisibility(View.GONE);
        searched_result_buildId_textView.setVisibility(View.GONE);
        preprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
    }
    private void show_preprining(){
        projectID_editText.setVisibility(View.VISIBLE);
        partnumber_editText.setVisibility(View.VISIBLE);
        numberofparts_editText.setVisibility(View.VISIBLE);
        printingparameters_editText.setVisibility(View.VISIBLE);
        comment_editText.setVisibility(View.VISIBLE);
        preprinting_expand_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shrink, 0);
        projectID_Text.setVisibility(View.VISIBLE);
        partnumber_Text.setVisibility(View.VISIBLE);
        numberofparts_Text.setVisibility(View.VISIBLE);
        printingparameters_Text.setVisibility(View.VISIBLE);
        comment_Text.setVisibility(View.VISIBLE);
        searched_result_projectAcronym_editText.setVisibility(View.VISIBLE);
        searched_result_buildId_editText.setVisibility(View.VISIBLE);
        searched_result_projectAcronym_textView.setVisibility(View.VISIBLE);
        searched_result_buildId_textView.setVisibility(View.VISIBLE);
    }
    private void hide_prining() {
        starttime_editText.setVisibility(View.GONE);
        endtime_editText.setVisibility(View.GONE);
        date_editText.setVisibility(View.GONE);
        operator_editText.setVisibility(View.GONE);
        typeofmachine_editText.setVisibility(View.GONE);
        powerweight_editText.setVisibility(View.GONE);
        powerweightatEnd_editText.setVisibility(View.GONE);
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
        //slmid_editText.setVisibility(View.VISIBLE);
        starttime_editText.setVisibility(View.VISIBLE);
        endtime_editText.setVisibility(View.VISIBLE);
        date_editText.setVisibility(View.VISIBLE);
        operator_editText.setVisibility(View.VISIBLE);
        typeofmachine_editText.setVisibility(View.VISIBLE);
        powerweight_editText.setVisibility(View.VISIBLE);
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
        //slmid_Text.setVisibility(View.VISIBLE);
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
        postID_Text.setVisibility(View.GONE);
        postID_editText.setVisibility(View.GONE);
        urlphoto_Text.setVisibility(View.GONE);
        urlphoto_editText.setVisibility(View.GONE);
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

        postID_Text.setVisibility(View.VISIBLE);
        postID_editText.setVisibility(View.VISIBLE);
        //TODO: add Project ID here ----
        urlphoto_Text.setVisibility(View.VISIBLE);
        urlphoto_editText.setVisibility(View.VISIBLE);
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
    private void reset(){
        reset_printing();
        reset_PrePrinting();
    }
    private void reset_printing(){
        String set_to = null;
        starttime_editText.setText(set_to);
        endtime_editText.setText(set_to);
        date_editText.setText(set_to);
        operator_editText.setText(set_to);
        typeofmachine_editText.setText(set_to);
        powerweight_editText.setText(set_to);
        powerweightatEnd_editText.setText(set_to);
        powderwaste_editText.setText(set_to);
        material_editText.setText(set_to);
        buildplatform_editText.setText(set_to);
        printTune_editText.setText(set_to);
        reused_times_editText.setText(set_to);
        numberofLayers_editText.setText(set_to);
        dpcFactor_editText.setText(set_to);
        minExposureTime_editText.setText(set_to);
        printingComments_editText.setText(set_to);
    }

    private void make_editable_uneditable(boolean editable){
        if(editable) submit_Button.setVisibility(View.VISIBLE);
        else submit_Button.setVisibility(View.GONE);
        projectID_editText.setEnabled(editable);
        partnumber_editText.setEnabled(editable);
        numberofparts_editText.setEnabled(editable);
        printingparameters_editText.setEnabled(editable);
        comment_editText.setEnabled(editable);
        //projectID_Text.setEnabled(editable);
        //partnumber_Text.setEnabled(editable);
        //numberofparts_Text.setEnabled(editable);
        //printingparameters_Text.setEnabled(editable);
        //comment_Text.setEnabled(editable);
        searched_result_projectAcronym_editText.setEnabled(editable);
        searched_result_buildId_editText.setEnabled(editable);

        //postID_Text.setEnabled(editable);
        postID_editText.setEnabled(editable);
        //urlphoto_Text.setEnabled(editable);
        urlphoto_editText.setEnabled(editable);
        //supportremoval_Text.setEnabled(editable);
        supportremovalSpinner.setEnabled(editable);
        //WEDM_Text.setEnabled(editable);
        WEDMSpinner.setEnabled(editable);
        //WEDMcomments_Text.setEnabled(editable);
        WEDMcomments_editText.setEnabled(editable);
        //blasting_Text.setEnabled(editable);
        blastingSpinner.setEnabled(editable);
        //blastingType_Text.setEnabled(editable);
        blastingType_editText.setEnabled(editable);
        //blastingTime_Text.setEnabled(editable);
        blastingTime_editText.setEnabled(editable);
        //blastingComment_Text.setEnabled(editable);
        blastingComment_editText.setEnabled(editable);
        //heatTreatment_Text.setEnabled(editable);
        //stressRelieving_Text.setEnabled(editable);
        //stressTemp_Text.setEnabled(editable);
        stressTemp_editText.setEnabled(editable);
        //stressTime_Text.setEnabled(editable);
        stressTime_editText.setEnabled(editable);
        //stressShieldingGas_Text.setEnabled(editable);
        shieldingSpinner.setEnabled(editable);
        //stressComment_Text.setEnabled(editable);
        stressComment_editText.setEnabled(editable);
        //hardening_Text.setEnabled(editable);
        //hardeningTemp_Text.setEnabled(editable);
        hardeningTemp_editText.setEnabled(editable);
        //hardeningTime_Text.setEnabled(editable);
        hardeningTime_editText.setEnabled(editable);
        //hardeningComment_Text.setEnabled(editable);
        hardeningComment_editText.setEnabled(editable);
        //tempering_Text.setEnabled(editable);
        //temperingTemp_Text.setEnabled(editable);
        temperingTemp_editText.setEnabled(editable);
        //temperingTime_Text.setEnabled(editable);
        temperingTime_editText.setEnabled(editable);
        //temperingNumberofCycles_Text.setEnabled(editable);
        temperingNumberofCycles_editText.setEnabled(editable);
        //temperingComment_Text.setEnabled(editable);
        temperingComment_editText.setEnabled(editable);
        //solutionTreatment_Text.setEnabled(editable);
        //solutionTreatmentTemp_Text.setEnabled(editable);
        solutionTreatmentTemp_editText.setEnabled(editable);
        //solutionTreatmentTime_Text.setEnabled(editable);
        solutionTreatmentTime_editText.setEnabled(editable);
        //solutionTreatmentComment_Text.setEnabled(editable);
        solutionTreatmentComment_editText.setEnabled(editable);
        //agingTreatment_Text.setEnabled(editable);
        //agingTemp_Text.setEnabled(editable);
        agingTemp_editText.setEnabled(editable);
        //agingTime_Text.setEnabled(editable);
        agingTime_editText.setEnabled(editable);
        //agingNumberofCycles_Text.setEnabled(editable);
        agingNumberofCycles_editText.setEnabled(editable);
        //agingComment_Text.setEnabled(editable);
        agingComment_editText.setEnabled(editable);

        //slmid_editText.setEnabled(editable);
        starttime_editText.setEnabled(editable);
        endtime_editText.setEnabled(editable);
        date_editText.setEnabled(editable);
        operator_editText.setEnabled(editable);
        typeofmachine_editText.setEnabled(editable);
        powerweight_editText.setEnabled(editable);
        powerweightatEnd_editText.setEnabled(editable);
        powderwaste_editText.setEnabled(editable);
        material_editText.setEnabled(editable);
        buildplatform_editText.setEnabled(editable);
        printTune_editText.setEnabled(editable);
        numberofLayers_editText.setEnabled(editable);
        dpcFactor_editText.setEnabled(editable);
        minExposureTime_editText.setEnabled(editable);
        printingComments_editText.setEnabled(editable);
        spinner.setEnabled(editable);
        //slmid_Text.setEnabled(editable);
        //starttime_Text.setEnabled(editable);
        //endtime_Text.setEnabled(editable);
        //date_Text.setEnabled(editable);
        //operator_Text.setEnabled(editable);
        //typeofmachine_Text.setEnabled(editable);
        //powerweight_Text.setEnabled(editable);
        //powerweightatEnd_Text.setEnabled(editable);
        //powderwaste_Text.setEnabled(editable);
        //material_Text.setEnabled(editable);
        //buildplatform_Text.setEnabled(editable);
        //printTune_Text.setEnabled(editable);
        //powderCondition_Text.setEnabled(editable);
        //numberofLayers_Text.setEnabled(editable);
        //dpcFactor_Text.setEnabled(editable);
        //minExposureTime_Text.setEnabled(editable);
        //printingComments_Text.setEnabled(editable);
    }

    private void show_preview() throws ExecutionException, InterruptedException {
        String url = url_server_magic + submitted_slm_id + ".png";
        CheckIfFileExists checkIfFileExists = new CheckIfFileExists();
        if(checkIfFileExists.execute(url).get()) {
            Bitmap drawable = null;
            try {
                drawable = new FetchMagic().execute(url).get();
                Log.d("magic", drawable + "");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("Problem FetchMagic", url + "");
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.d("Problem FetchMagic 2", url + "");
            }
            imageview.setImageBitmap(drawable);
        }
        else {
            Drawable drawable = this.getResources().getDrawable(R.drawable.no_magic);
            imageview.setImageDrawable(drawable);
        }
    }
    private void show_preview_postprinting() throws ExecutionException, InterruptedException {
        String url = url_server_postprint + submitted_slm_id + ".png";
        CheckIfFileExists checkIfFileExists = new CheckIfFileExists();
        if(checkIfFileExists.execute(url).get()) {
            Bitmap drawable = null;
            try {
                drawable = new FetchMagic().execute(url).get();
                Log.d("Post printing", drawable + "");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("Problem Fetch Snapshot", url + "");
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.d("Problem FetchSnapshot 2", url + "");
            }
            image_preview_post_printing.setImageBitmap(drawable);
        }
        else {
            Drawable drawable = this.getResources().getDrawable(R.drawable.no_magic);
            image_preview_post_printing.setImageDrawable(drawable);
        }
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFile(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String downloaded_path = "";
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                //downloaded_path = "/storage/emulated/0/PrintManager/Files/" + sUrl[1];
               // String PATH = Environment.getExternalStorageDirectory().toString() + "/printbook";
                String PATH = context.getFilesDir().toString();
                Log.v("LOG_TAG", "PATH: " + PATH);

                File file = new File(PATH);
                //file.mkdirs();
                File _downloaded_path = new File(file + "/" + sUrl[1]);
                downloaded_path = _downloaded_path.toString();
                Log.d("xxxxxxxxxxxxx", _downloaded_path.toString());
                output = new FileOutputStream(_downloaded_path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return downloaded_path;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private int update_preprinting(){
        String partnumber = partnumber_editText.getText().toString();
        String slm_id = submitted_slm_id;
        String buildId = searched_result_buildId_editText.getText().toString();
        String numberofparts = numberofparts_editText.getText().toString();
        String printingparameters = printingparameters_editText.getText().toString();
        String comment = comment_editText.getText().toString();
        if(TextUtils.isEmpty(buildId)) buildId = "null";
        if(TextUtils.isEmpty(numberofparts)) numberofparts = "null";
        if(TextUtils.isEmpty(printingparameters)) printingparameters = "null";
        if(TextUtils.isEmpty(comment)) comment = "null";
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id));
        params.add(new BasicNameValuePair(config.PREPRINTING_build_id, buildId));
        params.add(new BasicNameValuePair(config.PREPRINTING_no_parts, numberofparts));
        params.add(new BasicNameValuePair(config.PRINTING_printing_partnumber, "\"" + partnumber + "\""));
        params.add(new BasicNameValuePair(config.PREPRINTING_printing_parameter, printingparameters));
        params.add(new BasicNameValuePair(config.PREPRINTING_comment, comment));
        Log.d(TAG, "params" + params);
        Insert insert = new Insert(params, config.TAG_UPDATE_PREPRINTING);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }
    private int update_printing() {
        String starttime = starttime_editText.getText().toString();
        String endtime = endtime_editText.getText().toString();
        String date = date_editText.getText().toString();
        String powerweight = powerweight_editText.getText().toString();
        String powderatend = powerweightatEnd_editText.getText().toString();
        String powderused=null;
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
        if(!(TextUtils.isEmpty(powerweight) || TextUtils.isEmpty(powderatend)))
        {
            if(!(powerweight.equals("null") || powderatend.equals("null"))) {
                powderused = "" + (Integer.parseInt(powerweight_editText.getText().toString()) - Integer.parseInt(powerweightatEnd_editText.getText().toString()));
            }
            }
        if(TextUtils.isEmpty(powerweight))
        {
            powerweight = "null";
            powderused = "null";
        }
        if(TextUtils.isEmpty(powderatend)){
            powderatend = "null";
            powderused = "null";
        }
        if(TextUtils.isEmpty(powderwaste)) powderwaste = "null";
        if(TextUtils.isEmpty(buildplatform)) buildplatform = "null";
        if(TextUtils.isEmpty(printTime)) printTime = "null";
        if(TextUtils.isEmpty(reused_times)) reused_times = "null";
        if(TextUtils.isEmpty(numberofLayers)) numberofLayers = "null";
        if(TextUtils.isEmpty(dpcFactor)) dpcFactor = "null";
        if(TextUtils.isEmpty(minExposureTime)) minExposureTime = "null";
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //AUTO INCREMENT params.add(new BasicNameValuePair(config.PRINTING_printing_id, 2 + ""));
        params.add(new BasicNameValuePair(config.PRINTING_slm_id, submitted_slm_id));
        params.add(new BasicNameValuePair(config.PRINTING_start_time, starttime));
        params.add(new BasicNameValuePair(config.PRINTING_end_time, endtime));
        params.add(new BasicNameValuePair(config.PRINTING_date, date));
        params.add(new BasicNameValuePair(config.PRINTING_operator, operator_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_machine_type, typeofmachine_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.PRINTING_powder_weight_start, powerweight));
        params.add(new BasicNameValuePair(config.PRINTING_powder_weight_end, powderatend));
        params.add(new BasicNameValuePair(config.PRINTING_powder_used, powderused));
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
        Insert insert = new Insert(params, config.TAG_UPDATE_PRINTING);
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

    private int update_project() {
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String projectId = projectID_editText.getText().toString();
        String slm_id_project = submitted_slm_id;
        if(projectId.isEmpty()) projectId = "null";
        params.add(new BasicNameValuePair(config.PROJECT_number, projectId));
        params.add(new BasicNameValuePair(config.PROJECT_slm_id, slm_id_project));
        params.add(new BasicNameValuePair(config.PROJECT_name, searched_result_projectAcronym_editText.getText().toString()));
        Insert insert = new Insert(params, config.TAG_UPDATE_PROJECT);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;

    }

    private int update_postprinting() {
        String blastingTime = blastingTime_editText.getText().toString();
        if(TextUtils.isEmpty(blastingTime)) blastingTime = "null";
        int success = -1;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //AUTO INCREMENT params.add(new BasicNameValuePair(config.PRINTING_printing_id, 2 + ""));
        params.add(new BasicNameValuePair(config.POSTPRINTING_slm_id, submitted_slm_id));
        params.add(new BasicNameValuePair(config.POSTPRINTING_support_removal, supportremovalSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_wedm, WEDMSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_wedm_comment, WEDMcomments_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting, blastingSpinner.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_time, blastingTime));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_type, blastingType_editText.getText().toString()));
        params.add(new BasicNameValuePair(config.POSTPRINTING_blasting_comment, blastingComment_editText.getText().toString()));
        Insert insert = new Insert(params, config.TAG_UPDATE_POSTPRINTING);
        try {
            success = insert.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }
}