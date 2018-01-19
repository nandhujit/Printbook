package android.app.printerapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.printerapp.history.HistoryDrawerAdapter;
import android.app.printerapp.history.SwipeDismissListViewTouchListener;
import android.app.printerapp.library.LibraryController;
import android.app.printerapp.library.LibraryFragment;
import android.app.printerapp.library.detail.DetailViewFragment;
import android.app.printerapp.util.ui.AnimationHelper;
import android.app.printerapp.viewer.ViewerMainFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by alberto-baeza on 1/21/15.
 */
public class MainActivity extends ActionBarActivity {

    //List of Fragments
    private LibraryFragment mLibraryFragment; //Storage fragment
    private ViewerMainFragment mViewerFragment; //Print panel fragment @static for model load

    //Class specific variables
    private static Fragment mCurrent; //The current shown fragment @static
    private static FragmentManager mManager; //Fragment manager to handle transitions @static

    private static TabHost mTabHost;

    //Drawer
    private static DrawerLayout mDrawerLayout;
    private HistoryDrawerAdapter mDrawerAdapter;
    private static ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Since API level 11, thread policy has changed and now does not allow network operation to
         * be executed on UI thread (NetworkOnMainThreadException), so we have to add these lines to
         * permit it.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mTabHost = (TabHost) findViewById(R.id.tabHost);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Initialize variables
        mManager = getFragmentManager();

        //Initialize fragments
        mLibraryFragment = (LibraryFragment) getFragmentManager().findFragmentByTag(ListContent.ID_LIBRARY);
        mViewerFragment = (ViewerMainFragment) getFragmentManager().findFragmentByTag(ListContent.ID_VIEWER);


        initDrawer();


        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mAdapterNotification,
                new IntentFilter("notify"));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);


        mManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

        //Init gcode cache
    
        //Set tab host for the view
        setTabHost();
        String s = getIntent().getStringExtra("path");
        Log.d("first am here","path : " + s );
        if(!s.equals("path")){
            Log.d("am here",s);
            requestOpenFile(s);
        }

    }

    public static void performClick(int i){

        mTabHost.setCurrentTab(i);

    }

    //Initialize history drawer
    public void initDrawer(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                mDrawerLayout,                /* DrawerLayout object */
                R.string.add,            /* "open drawer" description */
                R.string.cancel         /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {


                if (slideOffset == 1.0){
                    mDrawerAdapter.notifyDataSetChanged();
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        LayoutInflater inflater = getLayoutInflater();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void setTabHost() {

        mTabHost.setup();

        //Models tab
        TabHost.TabSpec spec = mTabHost.newTabSpec("Library");
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_models)));
        spec.setContent(R.id.maintab1);
        mTabHost.addTab(spec);

        //Print panel tab
        spec = mTabHost.newTabSpec("Panel");
        spec.setIndicator(getTabIndicator(getResources().getString(R.string.fragment_print)));
        spec.setContent(R.id.maintab2);
        mTabHost.addTab(spec);


        mTabHost.setCurrentTab(0);
        onItemSelected(0);


        mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                View currentView = mTabHost.getCurrentView();
                AnimationHelper.inFromRightAnimation(currentView);

                onItemSelected(mTabHost.getCurrentTab());

                //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            }
        });

    }

    //handle action bar menu open
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    /**
     * Return the custom view of the tab
     *
     * @param title Title of the tab
     * @return Custom view of a tab layout
     */
    private View getTabIndicator(String title) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.main_activity_tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_title_textview);
        tv.setText(title);
        return view;
    }

    public void onItemSelected(int id) {

        if (id!= 1) {

            ViewerMainFragment.hideActionModePopUpWindow();
            ViewerMainFragment.hideCurrentActionPopUpWindow();
        }

        //start transaction
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();


        //Pop backstack to avoid having bad references when coming from a Detail view
        mManager.popBackStack();

        //If there is a fragment being shown, hide it to show the new one
        if (mCurrent != null) {
            try {
                fragmentTransaction.hide(mCurrent);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        //Select fragment
        switch (id) {

            case 0: {
                closePrintView();
                //Check if we already created the Fragment to avoid having multiple instances
                if (getFragmentManager().findFragmentByTag(ListContent.ID_LIBRARY) == null) {
                    mLibraryFragment = new LibraryFragment();
                    fragmentTransaction.add(R.id.maintab1, mLibraryFragment, ListContent.ID_LIBRARY);
                }
                mCurrent = mLibraryFragment;
            }

            break;
            case 1: {
                closePrintView();
                closeDetailView();
                //Check if we already created the Fragment to avoid having multiple instances
                if (getFragmentManager().findFragmentByTag(ListContent.ID_VIEWER) == null) {
                    mViewerFragment = new ViewerMainFragment();
                    fragmentTransaction.add(R.id.maintab2, mViewerFragment, ListContent.ID_VIEWER);
                }
                mCurrent = mViewerFragment;
            }
            break;
        }

        if (mViewerFragment != null) {
            if (mCurrent != mViewerFragment) {
                //Make the surface invisible to avoid frame overlapping
                mViewerFragment.setSurfaceVisibility(0);
            } else {
                //Make the surface visible when we press
                mViewerFragment.setSurfaceVisibility(1);
            }
        }

        //Show current fragment
        if (mCurrent != null) {
            fragmentTransaction.show(mCurrent).commit();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }


    }

    public static void refreshDevicesCount(){

        mCurrent.setMenuVisibility(false);




    }


    private static void closePrintView(){
        //Refresh printview fragment if exists
        Fragment fragment = mManager.findFragmentByTag(ListContent.ID_PRINTVIEW);

        if (mCurrent!=null) mCurrent.setMenuVisibility(true);
    }

    public static void closeDetailView(){
        //Refresh printview fragment if exists
        Fragment fragment = mManager.findFragmentByTag(ListContent.ID_DETAIL);
        if (fragment != null) ((DetailViewFragment) fragment).removeRightPanel();
    }

    /**
     * Override to allow back navigation on the Storage fragment.
     */
    @Override
    public void onBackPressed() {

        //Update the actionbar to show the up carat/affordance
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        if (mCurrent != null) {
            Fragment fragment = mManager.findFragmentByTag(ListContent.ID_SETTINGS);

            if ((fragment != null) ){

                closePrintView();

                if (mManager.popBackStackImmediate()){

                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    //Basically refresh printer count if all were deleted in Settings mode


                }else super.onBackPressed();
            } else super.onBackPressed();


        } else {
            super.onBackPressed();
        }
    }

    /**
     * Send a file to the Viewer to display
     *
     * @param path File path
     */
    public static void requestOpenFile(final String path) {

        //This method will simulate a click and all its effects
        performClick(1);

        //Handler will avoid crash
        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {

                if (path!=null) ViewerMainFragment.openFileDialog(path);
            }
        });

    }


    //notify ALL adapters every time a notification is received
    private BroadcastReceiver mAdapterNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            if (message!=null)
                if (message.equals("Devices")){


                    //Refresh printview fragment if exists
                    Fragment fragment = mManager.findFragmentByTag(ListContent.ID_PRINTVIEW);

                } else if (message.equals("Profile")){

                    if (mViewerFragment!=null) {
                        mViewerFragment.notifyAdapter();
                    }

                } else if (message.equals("Files")){

                    if (mLibraryFragment!=null) mLibraryFragment.refreshFiles();

                }

        }
    };

    /*
Close app on locale change
 */
    private BroadcastReceiver mLocaleChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            System.exit(0);

        }
    };

    @Override
    protected void onDestroy() {

        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAdapterNotification);

        super.onDestroy();
    }

    @Override
    protected void onResume() {


        super.onResume();

    }

    @Override
    protected void onPause() {



        super.onPause();

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("path").equals("requestOpenFile")){
            requestOpenFile("/storage/emulated/0/PrintManager/Files/Kcs_clamp_left/_stl/Kcs_clamp_left.stl");
        }
    }
}
