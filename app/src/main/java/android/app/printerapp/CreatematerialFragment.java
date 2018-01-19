package android.app.printerapp;

import android.Manifest;
import android.app.printerapp.database.Config;
import android.app.printerapp.database.Insert;
import android.app.printerapp.database.PathUtil;
import android.app.printerapp.database.UploadFilesAsync;
import android.app.printerapp.login.MainActivityNew;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Importing UploadService Package.
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class  CreatematerialFragment extends AppCompatActivity {

    // Creating Buttons.
    Button SelectButton, UploadButton;

    // Creating EditText.
    EditText PdfNameEditText ;

    // Creating URI .
    Uri uri;
    String path = "";
    //String folder_path ="http://10.0.2.2:8081/PrinterBook/PdfUploadFolder/";
    String folder_path = "https://group5sep.000webhostapp.com/PdfUploadFolder";
    View focusView = null;
    Config config;

    // Pdf upload request code.
    public int PDF_REQ_CODE = 1;

    // Define strings to hold given pdf name, path and ID.
    String PdfNameHolder, PdfPathHolder, PdfID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_material);

        // Method to enable runtime permission.
       // RequestRunTimePermission();

        // Assign ID'S to button and EditText.
        SelectButton = (Button) findViewById(R.id.select);
        UploadButton = (Button) findViewById(R.id.upload_id);
        PdfNameEditText = (EditText) findViewById(R.id.the_material_id);
        PdfNameEditText.setEnabled(false);

        // Adding click listener to Button.
        SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfID = UUID.randomUUID().toString();

                // PDF selection code start from here .
                // Creating intent object.
                Intent intent = new Intent();

                // Setting up default file pickup time as PDF.
                intent.setType("application/pdf");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);

            }
        });

        // Adding click listener to Upload PDF button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(path.isEmpty() || !SelectButton.getText().toString().equals("PDF is Selected")){
                    SelectButton.setError(getString(R.string.error_field_required));
                    focusView = SelectButton;
                    focusView.requestFocus();
                }else{
                    // Calling method to upload PDF on server.
                    int success = PdfUploadFunction();
                    int success_2 = -1;
                    if(success == 1 ){
                        String hosted_path = folder_path + "" + PdfID + ".pdf";
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Material_ID", PdfID));
                        params.add(new BasicNameValuePair("URL_Image", hosted_path));
                        Insert insert = new Insert(params, "INSERT TO MATERIAL");
                        try {
                            success_2 = insert.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    if(success_2 == 1){
                        Intent intent = new Intent(CreatematerialFragment.this, MainActivityNew.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(CreatematerialFragment.this,"PROBLEM PROBLEM UPLOADING",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            try {
                PdfPathHolder = PathUtil.getPath(this,uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            path = uri.toString();

            // After selecting the PDF set PDF is Selected text inside Button.
            SelectButton.setText("PDF is Selected");
            PdfNameEditText.setText(PdfID);
        }
    }

    // PDF upload function starts from here.
    public int PdfUploadFunction() {
        int success_upload = -1;
        // Getting pdf name from EditText.
        PdfNameHolder = PdfID; //PdfNameEditText.getText().toString().trim();

        // Getting file path using Filepath class.
        //PdfPathHolder = FilePath.getPath(this, uri);
        Log.d("path",PdfPathHolder);

        // If file path object is null then showing toast message to move file into internal storage.
        if (PdfPathHolder == null) {
            Toast.makeText(this, "Please move your PDF file to internal storage & try again.", Toast.LENGTH_LONG).show();

        }
        // If file path is not null then PDF uploading file process will starts.
        else try {
                    //UploadPdf uploadPdf = new UploadPdf();
                    //uploadPdf.execute(this).get();
            UploadFilesAsync uploadFilesAsync = new UploadFilesAsync(PdfPathHolder, PdfID,".pdf",config.url_upload_pdf);
            try {
                success_upload = uploadFilesAsync.execute().get();
                Log.d("success_upload",success_upload + "");
            } catch (InterruptedException e) {
                Log.d("ERROR ERROR",e.toString());
                e.printStackTrace();
            } catch (ExecutionException e) {
                Log.d("ERROR ERROR",e.toString());
                e.printStackTrace();
            }
        } catch (Exception exception) {

            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Exception", exception.getMessage());
        }
        return success_upload;
    }


    // Requesting run time permission method starts from here.
    public void RequestRunTimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(CreatematerialFragment.this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {

            Toast.makeText(CreatematerialFragment.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(CreatematerialFragment.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(CreatematerialFragment.this,"Permission Granted", Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(CreatematerialFragment.this, "Permission Canceled", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private class UploadPdf extends AsyncTask<Context,String,String>{

        @Override
        protected String doInBackground(Context...contexts) {
            String result = null;
            try {
                Log.d("context", contexts[0] + "");
                MultipartUploadRequest upload = new MultipartUploadRequest(contexts[0], "PDF_UPLOAD_HTTP_URL")
                        .addFileToUpload(PdfPathHolder, "pdf")
                        .addParameter("name", PdfNameHolder)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5);
                Log.d("uploadxxxxxx",upload +"");
                upload.startUpload();
            } catch (MalformedURLException e) {
                Log.d("error",e.toString());
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                Log.d("error2",e.toString());
                e.printStackTrace();
            }
            Log.d("result", result);
            return result;
        }
    }


}
