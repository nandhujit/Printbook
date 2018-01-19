package android.app.printerapp.viewer.sidepanel;

import android.app.Activity;
import android.app.printerapp.R;
import android.app.printerapp.model.ModelProfile;
import android.app.printerapp.viewer.SlicingHandler;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to initialize and handle the side panel in the print panel
 * Created by alberto-baeza on 10/24/14.
 */
public class SidePanelHandler {

    //static parameters

    private static final int DEFAULT_INFILL = 20;
    private int mCurrentInfill = DEFAULT_INFILL;
    private Activity mActivity;

    public SidePanelProfileAdapter profileAdapter;

    //Constructor
    public SidePanelHandler(SlicingHandler handler, Activity activity, View v) {

        mActivity = activity;
    }


    /**
     * Parse float to a variable to avoid accuracy error
     *
     * @param s
     * @return
     */
    public Float getFloatValue(String s) throws NumberFormatException {

        Float f = Float.parseFloat(s);

        return f;
    }

    /**
     * Save a slicing profile by adding every individual element to a JSON
     */
    public void saveProfile() {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View getModelsDialogView = inflater.inflate(R.layout.dialog_create_profile, null);
        final MaterialEditText nameEditText = (MaterialEditText) getModelsDialogView.findViewById(R.id.new_profile_name_edittext);

        final MaterialDialog.Builder createFolderDialog = new MaterialDialog.Builder(mActivity);
        createFolderDialog.title(R.string.dialog_create_profile_title)
                .customView(getModelsDialogView, true)
                .positiveColorRes(R.color.theme_accent_1)
                .positiveText(R.string.create)
                .negativeColorRes(R.color.body_text_2)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String name = nameEditText.getText().toString().trim();
                        if (name == null || name.equals("")) {
                            nameEditText.setError(mActivity.getString(R.string.library_create_folder_name_error));
                        }
                        else {
                            //Init UI elements

                            JSONObject profile = null;


                            //Parse the JSON element
                            try {

                                //Profile info
                                profile = new JSONObject();

                                profile.put("displayName", nameEditText.getText().toString());
                                profile.put("description", "Test profile created from App"); //TODO
                                profile.put("key", nameEditText.getText().toString().replace(" ", "_").toLowerCase());

                                //Data info
                                JSONObject data = new JSONObject();

                                profile.put("data", data);


                                //Log.i("OUT", profile.toString());


                            } catch (JSONException e) {
                                e.printStackTrace();

                            } catch (NumberFormatException e) {

                                //Check if there was an invalid number
                                e.printStackTrace();
                                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                                profile = null;

                            }

                            if (profile != null) {


                                //check if name already exists to avoid overwriting
                                for (String s : ModelProfile.getQualityList()) {

                                    try {
                                        if (profile.get("displayName").equals(s)) {

                                            Toast.makeText(mActivity, mActivity.getString(R.string.printview_profiles_overwrite) + ": " + s, Toast.LENGTH_LONG).show();

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                }}

                        dialog.dismiss();
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                })
                .show();




    }


    public void reloadProfileAdapter(){

        ModelProfile.reloadList(mActivity);

        ArrayAdapter mProfileAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.print_panel_spinner_item, ModelProfile.getProfileList());
        mProfileAdapter.setDropDownViewResource(R.layout.print_panel_spinner_dropdown_item);

        if (mProfileAdapter!=null){
           mProfileAdapter.notifyDataSetChanged();
        }



    }

    /**********************************************************************************************/


    /**
     * Only works for "extra" profiles, add a new value per field since we can't upload them yet
     */
    //TODO Temporary



    /**
     * Clear the extra parameter list and reload basic parameters
     */

    /**
     * Generic text watcher to add new printing parameters
     */
    private class GenericTextWatcher implements TextWatcher, CompoundButton.OnCheckedChangeListener {

        private String mValue;

        private GenericTextWatcher(String v) {

            mValue = v;

        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }


        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        }
    }



}
