package com.beloko.touchcontrols;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ControlConfig implements Serializable {


    public static final int LOOK_MODE_MOUSE = 0;
    public static final int LOOK_MODE_ABSOLUTE = 1;
    public static final int LOOK_MODE_JOYSTICK = 2;

    public static final int ACTION_ANALOG_FWD = 0x100;
    public static final int ACTION_ANALOG_STRAFE = 0x101;
    public static final int ACTION_ANALOG_PITCH = 0x102;
    public static final int ACTION_ANALOG_YAW = 0x103;
    public static final int PORT_ACT_LEFT = 1;
    public static final int PORT_ACT_RIGHT = 2;
    public static final int PORT_ACT_FWD = 3;
    public static final int PORT_ACT_BACK = 4;
    public static final int PORT_ACT_LOOK_UP = 5;
    public static final int PORT_ACT_LOOK_DOWN = 6;
    public static final int PORT_ACT_MOVE_LEFT = 7;
    public static final int PORT_ACT_MOVE_RIGHT = 8;
    public static final int PORT_ACT_STRAFE = 9;
    public static final int PORT_ACT_SPEED = 10;
    public static final int PORT_ACT_USE = 11;
    public static final int PORT_ACT_JUMP = 12;
    public static final int PORT_ACT_ATTACK = 13;
    public static final int PORT_ACT_UP = 14;
    public static final int PORT_ACT_DOWN = 15;
    public static final int PORT_ACT_NEXT_WEP = 16;
    public static final int PORT_ACT_PREV_WEP = 17;
    //Quake 2
    public static final int PORT_ACT_INVEN = 18;
    public static final int PORT_ACT_INVUSE = 19;
    public static final int PORT_ACT_INVDROP = 20;
    public static final int PORT_ACT_INVPREV = 21;
    public static final int PORT_ACT_INVNEXT = 22;
    public static final int PORT_ACT_HELPCOMP = 23;
    //Doom
    public static final int PORT_ACT_MAP = 30;
    public static final int PORT_ACT_MAP_UP = 31;
    public static final int PORT_ACT_MAP_DOWN = 32;
    public static final int PORT_ACT_MAP_LEFT = 33;
    public static final int PORT_ACT_MAP_RIGHT = 34;
    public static final int PORT_ACT_MAP_ZOOM_IN = 35;
    public static final int PORT_ACT_MAP_ZOOM_OUT = 36;
    //RTCW
    public static final int PORT_ACT_ZOOM_IN = 50;
    public static final int PORT_ACT_ALT_FIRE = 51;
    public static final int PORT_ACT_RELOAD = 52;
    public static final int PORT_ACT_QUICKSAVE = 53;
    public static final int PORT_ACT_QUICKLOAD = 54;
    public static final int PORT_ACT_KICK = 56;
    public static final int PORT_ACT_LEAN_LEFT = 57;
    public static final int PORT_ACT_LEAN_RIGHT = 58;
    //MALICE
    public static final int PORT_MALICE_USE = 59;
    public static final int PORT_MALICE_RELOAD = 60;
    public static final int PORT_MALICE_CYCLE = 61;
    //JK2
    //public static final int PORT_ACT_FORCE_LIGHTNING = 60;
    //public static final int PORT_ACT_SABER_BLOCK = 62;
    //public static final int PORT_ACT_FORCE_GRIP = 63;
    public static final int PORT_ACT_ALT_ATTACK = 64;
    public static final int PORT_ACT_NEXT_FORCE = 65;
    public static final int PORT_ACT_PREV_FORCE = 66;
    public static final int PORT_ACT_FORCE_USE = 67;
    public static final int PORT_ACT_DATAPAD = 68;
    public static final int PORT_ACT_FORCE_SELECT = 69;
    public static final int PORT_ACT_WEAPON_SELECT = 70;
    public static final int PORT_ACT_SABER_STYLE = 71;
    public static final int PORT_ACT_FORCE_PULL = 75;
    public static final int PORT_ACT_FORCE_MIND = 76;
    public static final int PORT_ACT_FORCE_LIGHT = 77;
    public static final int PORT_ACT_FORCE_HEAL = 78;
    public static final int PORT_ACT_FORCE_GRIP = 79;
    public static final int PORT_ACT_FORCE_SPEED = 80;
    public static final int PORT_ACT_FORCE_PUSH = 81;
    public static final int PORT_ACT_SABER_SEL = 87; //Just chooses weapon 1 so show/hide saber.
    //Chocolate
    public static final int PORT_ACT_GAMMA = 90;
    public static final int PORT_ACT_SHOW_WEAPONS = 91;
    public static final int PORT_ACT_SHOW_KEYS = 92;
    public static final int PORT_ACT_FLY_UP = 93;
    public static final int PORT_ACT_FLY_DOWN = 94;
    //Custom
    public static final int PORT_ACT_CUSTOM_0 = 150;
    public static final int PORT_ACT_CUSTOM_1 = 151;
    public static final int PORT_ACT_CUSTOM_2 = 152;
    public static final int PORT_ACT_CUSTOM_3 = 153;
    public static final int PORT_ACT_CUSTOM_4 = 154;
    public static final int PORT_ACT_CUSTOM_5 = 155;
    public static final int PORT_ACT_CUSTOM_6 = 156;
    public static final int PORT_ACT_CUSTOM_7 = 157;
    //Menu
    public static final int MENU_UP = 0x200;
    public static final int MENU_DOWN = 0x201;
    public static final int MENU_LEFT = 0x202;
    public static final int MENU_RIGHT = 0x203;
    public static final int MENU_SELECT = 0x204;
    public static final int MENU_BACK = 0x205;

    public enum Type {ANALOG, BUTTON, MENU}

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    final String LOG = "QuakeControlConfig";
    Context ctx;
    TextView infoTextView;
    String filename;
    boolean ignoreDirectionFromJoystick;
    ArrayList<ActionInput> actions = new ArrayList<>();
    ActionInput actionMonitor = null;
    boolean monitoring = false;
    int[] axisTest = {
            /*
            MotionEvent.AXIS_GENERIC_1,
            MotionEvent.AXIS_GENERIC_2,
            MotionEvent.AXIS_GENERIC_3,
            MotionEvent.AXIS_GENERIC_4,
            MotionEvent.AXIS_GENERIC_5,
            MotionEvent.AXIS_GENERIC_6,
            MotionEvent.AXIS_GENERIC_7,
            MotionEvent.AXIS_GENERIC_8,
            MotionEvent.AXIS_GENERIC_9,
            MotionEvent.AXIS_GENERIC_10,
            MotionEvent.AXIS_GENERIC_11,
            MotionEvent.AXIS_GENERIC_12,
            MotionEvent.AXIS_GENERIC_13,
            MotionEvent.AXIS_GENERIC_14,
            MotionEvent.AXIS_GENERIC_15,
            MotionEvent.AXIS_GENERIC_16,
             */
            MotionEvent.AXIS_HAT_X,
            MotionEvent.AXIS_HAT_Y,
            MotionEvent.AXIS_LTRIGGER,
            MotionEvent.AXIS_RTRIGGER,
            MotionEvent.AXIS_RUDDER,
            MotionEvent.AXIS_RX,
            MotionEvent.AXIS_RY,
            MotionEvent.AXIS_RZ,
            MotionEvent.AXIS_THROTTLE,
            MotionEvent.AXIS_X,
            MotionEvent.AXIS_Y,
            MotionEvent.AXIS_Z,
            MotionEvent.AXIS_BRAKE,
            MotionEvent.AXIS_GAS,
    };

    public ControlConfig(String file, ArrayList<ActionInput> gamepadActions) {
        // Conditional null check hotfix to prevent crashing
        //     no idea how to reproduce crash or what this may do
        if (gamepadActions != null && file != null) {
            actions.addAll(gamepadActions);
            filename = file;
        } else {
            Log.d(LOG, "TouchControls gamepadActions or file was null!");
        }
    }

    public void setTextView(Context c, TextView tv) {
        ctx = c;
        infoTextView = tv;
    }

    void saveControls() throws IOException {
        saveControls(new File(filename));
    }

    void saveControls(File file) throws IOException {
        if (TouchSettings.DEBUG) Log.d(LOG, "saveControls, file = " + file.toString());

        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        fos = new FileOutputStream(file);
        out = new ObjectOutputStream(fos);
        out.writeObject(actions);
        out.close();
    }

    public void loadControls() throws IOException, ClassNotFoundException {
        loadControls(new File(filename));
    }

    public void loadControls(File file) throws IOException, ClassNotFoundException {
        if (TouchSettings.DEBUG) Log.d(LOG, "loadControls, file = " + file.toString());

        InputStream fis = null;
        ObjectInputStream in = null;

        fis = new FileInputStream(file);

        in = new ObjectInputStream(fis);
        ArrayList<ActionInput> cd = (ArrayList<ActionInput>) in.readObject();
        if (TouchSettings.DEBUG) Log.d(LOG, "loadControls, file loaded OK");
        in.close();

        for (ActionInput d : cd) {
            for (ActionInput a : actions) {
                if (d.tag.contentEquals(a.tag)) {
                    a.invert = d.invert;
                    a.source = d.source;
                    a.sourceType = d.sourceType;
                    a.sourcePositive = d.sourcePositive;
                    a.scale = d.scale;
                    if (a.scale == 0) a.scale = 1;
                }
            }
        }

        //Now check no buttons are also assigned to analog, if it is, clear the buttons
        //This is because n00bs keep assigning movement analog AND buttons!
        for (ActionInput a : actions) {
            if ((a.source != -1) && (a.sourceType == Type.ANALOG) && (a.actionType == Type.BUTTON)) {
                for (ActionInput a_check : actions) {
                    if ((a_check.sourceType == Type.ANALOG) && (a_check.actionType == Type.ANALOG)) {
                        if (a.source == a_check.source) {
                            a.source = -1;
                            break;
                        }
                    }
                }
            }
        }

        fis.close();
    }

    void updated() {
        try {
            saveControls(new File(filename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean showExtraOptions(Activity act, int pos) {
        final ActionInput in = actions.get(pos);

        if (in.actionType == Type.ANALOG) {
            Dialog dialog = new Dialog(act);
            dialog.setTitle("Axis Sensitivity Setting");
            dialog.setCancelable(true);

            final LinearLayout l = new LinearLayout(act);
            l.setOrientation(LinearLayout.VERTICAL);

            final SeekBar sb = new SeekBar(act);
            l.addView(sb);


            sb.setMax(100);
            sb.setProgress((int) (in.scale * 50));

            final CheckBox invert = new CheckBox(act);
            invert.setText("Invert");
            invert.setChecked(in.invert);

            l.addView(invert);

            dialog.setOnDismissListener(dialog1 -> {
                in.scale = (float) sb.getProgress() / (float) 50;
                in.invert = invert.isChecked();
                updated();
            });

            dialog.setContentView(l);

            dialog.show();
            return true;
        }
        return false;
    }

    public void startMonitor(Activity act, int pos) {
        actionMonitor = actions.get(pos);
        monitoring = true;

        if (actionMonitor.actionType == Type.ANALOG)
            infoTextView.setText("Move Stick for: " + actionMonitor.description);
        else
            infoTextView.setText("Press Button for: " + actionMonitor.description);

        infoTextView.setTextColor(ctx.getResources().getColor(android.R.color.holo_green_light));
    }

    public boolean onGenericMotionEvent(GenericAxisValues event) {
        if (TouchSettings.DEBUG) Log.d(LOG, "onGenericMotionEvent");
        if (monitoring) {
            if (actionMonitor != null) {
                for (int a : axisTest) {
                    if (Math.abs(event.getAxisValue(a)) > 0.6) {
                        actionMonitor.source = a;
                        actionMonitor.sourceType = Type.ANALOG;
                        //Used for button actions
                        actionMonitor.sourcePositive = event.getAxisValue(a) > 0;

                        monitoring = false;

                        if (TouchSettings.DEBUG)
                            Log.d(LOG, actionMonitor.description + " = Analog (" + actionMonitor.source + ")");

                        infoTextView.setText("Select Action");
                        infoTextView.setTextColor(ctx.getResources().getColor(android.R.color.holo_blue_light));

                        updated();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (TouchSettings.DEBUG) Log.d(LOG, "onKeyDown " + keyCode);

        if (monitoring) {
            if (keyCode == KeyEvent.KEYCODE_BACK) //Cancel and clear button assignment
            {
                actionMonitor.source = -1;
                actionMonitor.sourceType = Type.BUTTON;
                monitoring = false;
                infoTextView.setText("CANCELED");
                infoTextView.setTextColor(ctx.getResources().getColor(android.R.color.holo_red_light));

                updated();
                return true;
            } else {
                if (actionMonitor != null) {
                    if (actionMonitor.actionType != Type.ANALOG) {
                        actionMonitor.source = keyCode;
                        actionMonitor.sourceType = Type.BUTTON;
                        monitoring = false;

                        infoTextView.setText("Select Action");
                        infoTextView.setTextColor(ctx.getResources().getColor(android.R.color.holo_blue_light));

                        updated();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public int getSize() {
        return actions.size();
    }

    public View getView(final Activity ctx, final int nbr) {

        View view = ctx.getLayoutInflater().inflate(R.layout.controls_listview_item, null);
        ImageView image = view.findViewById(R.id.imageView);
        TextView name = view.findViewById(R.id.name_textview);
        TextView binding = view.findViewById(R.id.binding_textview);
        ImageView setting_image = view.findViewById(R.id.settings_imageview);

        ActionInput ai = actions.get(nbr);

        if ((ai.actionType == Type.BUTTON) || (ai.actionType == Type.MENU)) {

            if (ai.sourceType == Type.ANALOG)
                binding.setText(MotionEvent.axisToString(ai.source));
            else
                binding.setText(KeyEvent.keyCodeToString(ai.source));

            setting_image.setVisibility(View.GONE);

            if ((ai.actionType == Type.MENU)) {
                name.setTextColor(0xFF00aeef); //BLUEY
                image.setImageResource(R.drawable.gamepad_menu);
            } else {
                image.setImageResource(R.drawable.gamepad);
            }
        } else if (ai.actionType == Type.ANALOG) {
            binding.setText(MotionEvent.axisToString(ai.source));
            setting_image.setOnClickListener(v -> showExtraOptions(ctx, nbr));
            name.setTextColor(0xFFf7941d); //ORANGE
        }

/*
        if (ai.actionType == Type.BUTTON) {
            image.setImageResource(R.drawable.gamepad);
            if (ai.sourceType == Type.ANALOG)
                binding.setText(MotionEvent.axisToString(ai.source));
            else
                binding.setText(KeyEvent.keyCodeToString(ai.source));

            setting_image.setVisibility(View.GONE);
        } else //Analog
        {
            binding.setText(MotionEvent.axisToString(ai.source));
            setting_image.setOnClickListener(v -> showExtraOptions(ctx, nbr));
            name.setTextColor(0xFFf7941d);
        }
*/

        name.setText(ai.description);

        return view;
    }

}
