package net.nullsum.freedoom;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;

import com.beloko.touchcontrols.GamePadFragment;

import java.util.Arrays;


public class EntryActivity extends FragmentActivity {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current tab position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    GamePadFragment gamePadFrag;
    LaunchFragmentGZdoom launchFragmentGZdoom;
    String LOG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        setContentView(R.layout.activity_quake);

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        AppSettings.reloadSettings(getApplication());

        Resources res = this.getResources();
        GamePadFragment.gamepadActions = Utils.getGameGamepadConfig(res);

        actionBar.addTab(actionBar.newTab().setText(R.string.app_name).setTabListener(new TabListener<>(this, "Gzdoom", LaunchFragmentGZdoom.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.gamepad_tab).setTabListener(new TabListener<>(this, "gamepad", GamePadFragment.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.options_tab).setTabListener(new TabListener<>(this, "options", OptionsFragment.class)));


        String last_tab = AppSettings.getStringOption(getApplicationContext(), "last_tab", "");
        actionBar.setSelectedNavigationItem(0);

        gamePadFrag = (GamePadFragment) getFragmentManager().findFragmentByTag("gamepad");
        launchFragmentGZdoom = (LaunchFragmentGZdoom) getFragmentManager().findFragmentByTag("Gzdoom");
    }

    public void restart(){
        Intent intent = new Intent(this, EntryActivity.class);
        this.startActivity(intent);
        this.finishAffinity();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_quake, menu);
        return true;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (gamePadFrag == null)
            gamePadFrag = (GamePadFragment) getFragmentManager().findFragmentByTag("gamepad");

        return gamePadFrag.onGenericMotionEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (gamePadFrag == null)
            gamePadFrag = (GamePadFragment) getFragmentManager().findFragmentByTag("gamepad");

        if (gamePadFrag.onKeyDown(keyCode, event))
            return true;
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (gamePadFrag == null)
            gamePadFrag = (GamePadFragment) getFragmentManager().findFragmentByTag("gamepad");

        if (gamePadFrag.onKeyUp(keyCode, event))
            return true;
        else
            return super.onKeyUp(keyCode, event);
    }

    public Context getActivity() {
        return this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // copy over Freedoom files
        Log.d(LOG, "Got permissions request result: " + Arrays.toString(permissions));
        Log.d(LOG, "grant results: " + Arrays.toString(grantResults));

        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AppSettings.createDirectories(this);

                launchFragmentGZdoom = (LaunchFragmentGZdoom) getFragmentManager().findFragmentByTag("Gzdoom");

                launchFragmentGZdoom.copyAndUpdate();

            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.file_permission_fail_toast),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

/*

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

*/

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final FragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(FragmentActivity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);

            if (mFragment == null) //Actually create all fragments NOW
            {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.add(android.R.id.content, mFragment, mTag);
                ft.commit();
            }


            //if (mFragment != null && !mFragment.isDetached()) {
            if (mFragment != null && !mFragment.isHidden()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                //ft.detach(mFragment);
                ft.hide(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                //ft.attach(mFragment);
                //ft.setCustomAnimations(R., R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                ft.show(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                //ft.detach(mFragment);
                ft.hide(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            //Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }

}
