package com.beloko.touchcontrols;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class GamePadFragment extends Fragment {
    //This is a bit shit, set this before instantiating the fragment
    public static ArrayList<ActionInput> gamepadActions;
    final String LOG = "GamePadFragment";
    ListView listView;
    ControlListAdapter adapter;
    TextView info;
    ControlConfig config;
    GenericAxisValues genericAxisValues = new GenericAxisValues();
    boolean isHidden = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new ControlConfig(TouchSettings.gamePadControlsFile, gamepadActions);

        try {
            config.loadControls();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();

            //Failed to load, so save the default
            try {
                config.saveControls();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        isHidden = hidden;
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_gamepad, null);


        CheckBox enableCb = mainView.findViewById(R.id.gamepad_enable_checkbox);
        enableCb.setChecked(TouchSettings.gamePadEnabled);

        enableCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TouchSettings.setBoolOption(getActivity(), "gamepad_enabled", isChecked);
            TouchSettings.gamePadEnabled = isChecked;
            setListViewEnabled(TouchSettings.gamePadEnabled);

        });


        CheckBox hideCtrlCb = mainView.findViewById(R.id.gamepad_hide_touch_checkbox);
        hideCtrlCb.setChecked(TouchSettings.hideTouchControls);

        hideCtrlCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TouchSettings.setBoolOption(getActivity(), "hide_touch_controls", isChecked);
            TouchSettings.hideTouchControls = isChecked;
        });


        Button help = mainView.findViewById(R.id.gamepad_help_button);
        help.setOnClickListener(v -> {
            //NoticeDialog.show(getActivity(), "Gamepad Help", R.raw.gamepad);
        });

        listView = mainView.findViewById(R.id.gamepad_listview);
        adapter = new ControlListAdapter(getActivity());
        listView.setAdapter(adapter);

        setListViewEnabled(TouchSettings.gamePadEnabled);


        listView.setSelector(R.drawable.layout_sel_background);
        listView.setOnItemClickListener((arg0, v, pos, id) -> config.startMonitor(getActivity(), pos));

        listView.setOnItemLongClickListener((arg0, v, pos, id) -> config.showExtraOptions(getActivity(), pos));

        adapter.notifyDataSetChanged();

        info = mainView.findViewById(R.id.gamepad_info_textview);
        info.setText("Select Action");
        info.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        config.setTextView(getActivity(), info);

        return mainView;
    }

    private void setListViewEnabled(boolean v) {

        listView.setEnabled(v);
        if (v) {
            listView.setAlpha(1);
        } else {
            listView.setAlpha(0.3f);
            //listView.setBackgroundColor(Color.GRAY);
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        genericAxisValues.setAndroidValues(event);

        if (config.onGenericMotionEvent(genericAxisValues))
            adapter.notifyDataSetChanged();

        //return config.isMonitoring(); //This does not work, mouse appears anyway
        return !isHidden; //If gamepad tab visible always steal
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (config.onKeyDown(keyCode, event)) {
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (config.onKeyUp(keyCode, event)) {
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    class ControlListAdapter extends BaseAdapter {
        private Activity context;

        public ControlListAdapter(Activity context) {
            this.context = context;
        }

        public void add(String string) {
        }

        public int getCount() {
            return config.getSize();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup list) {
            return config.getView(getActivity(), position);
        }

    }

}
