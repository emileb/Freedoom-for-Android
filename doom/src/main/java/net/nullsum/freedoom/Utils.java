package net.nullsum.freedoom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import com.beloko.touchcontrols.ActionInput;
import com.beloko.touchcontrols.ControlConfig;
import com.beloko.touchcontrols.ControlConfig.Type;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Utils {
    private static final int BUFFER_SIZE = 1024;
    private static String LOG = "Utils";
    private Resources res;

    public Utils(Resources res) {
        this.res = res;
    }

    static void copyFreedoomFilesToSD(Activity responsibleActivity) {


        String fullBaseDir = AppSettings.getQuakeFullDir();
        String fullWadDir = fullBaseDir + "/" + "wads";

        // Freedoom additions
        Utils.copyAsset(responsibleActivity, "freedoom1.wad", fullBaseDir);
        Utils.copyAsset(responsibleActivity, "freedoom2.wad", fullBaseDir);
        // Freedoom licence and credits
        Utils.copyAsset(responsibleActivity, "COPYING.txt", fullBaseDir);
        Utils.copyAsset(responsibleActivity, "CREDITS.txt", fullBaseDir);

        // Add Romero's Sigil addon wad
        Utils.copyAsset(responsibleActivity, "SIGIL_v1_21.wad", fullWadDir);
        // Credits
        Utils.copyAsset(responsibleActivity, "SIGIL_v1_21.txt", fullWadDir);

        // Add 10sector and 10sector2 wads, megawads with ultra small levels
        Utils.copyAsset(responsibleActivity, "10sector.wad", fullWadDir);
        Utils.copyAsset(responsibleActivity, "10sector.txt", fullWadDir);
        Utils.copyAsset(responsibleActivity, "10secto2.wad", fullWadDir);
        Utils.copyAsset(responsibleActivity,"10secto2.txt",fullWadDir);


        // copy a custom gzdoom iniFile to set midi device to fluidsynth
        String iniFileName = "zdoom.ini";
        String iniFolderName = "/gzdoom_dev";
        File tester = new File(fullBaseDir + iniFolderName + "/" + iniFileName);
        if (!tester.exists()) {
            Log.d(LOG, "zdoom.ini file not present, copying custom one");
            Utils.copyAsset(responsibleActivity, iniFileName, fullBaseDir + iniFolderName);
        } else {
            Log.d(LOG, "zdoom.ini file is already present");
        }

        // copy over gzdoom mod/package files
        String modFolderName = "mods";
        String fullModDir = fullBaseDir + "/" + modFolderName;

        tester = new File( fullModDir + "/" + "brightmaps.pk3");
        if (!tester.exists()) {
            Utils.copyAsset(responsibleActivity, "brightmaps.pk3", fullModDir);
        }

        tester = new File( fullModDir + "/" + "lights.pk3");
        if (!tester.exists()) {
            Utils.copyAsset(responsibleActivity, "lights.pk3", fullModDir);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
    }

    private static void copyFile(InputStream in, OutputStream out, ProgressDialog pb) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            pb.setProgress(pb.getProgress() + 1024);
        }
        out.close();
    }

    static public void showDownloadDialog(final Activity act, String title, final String KEY, final String directory, final String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setMessage(title)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    // Download stuff here
                });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String checkFiles(String basePath, String[] files_to_ceck) {
        File[] files = new File(basePath).listFiles();
        boolean ok = true;

        StringBuilder filesNotFound = new StringBuilder();

        String[] expected;
        expected = files_to_ceck;

        if (files == null)
            files = new File[0];

        for (File f : files) {
            Log.d(LOG, "FILES: " + f.toString());

        }

        for (String e : expected) {
            boolean found = false;
            for (File f : files) {
                if (f.toString().toLowerCase().endsWith(e.toLowerCase()))
                    found = true;
            }
            if (!found) {
                Log.d(LOG, "Didnt find " + e);
                filesNotFound.append(e).append("\n");
                ok = false;
            }
        }

        if (filesNotFound.toString().contentEquals(""))
            return null;
        else
            return filesNotFound.toString();

    }

    static void copyPNGAssets(Context ctx, String dir) {
        String prefix = "";

        File d = new File(dir);
        if (!d.exists())
            d.mkdirs();

        AssetManager assetManager = ctx.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            if (filename.endsWith("png")) {
                InputStream in = null;
                OutputStream out = null;
                //Log.d("test", "file = " + filename);
                try {
                    in = assetManager.open(filename);
                    out = new FileOutputStream(dir + "/" + filename.substring(prefix.length()));
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
            }
        }
    }

    public static void ExtractAsset(Context ctx, String file, String dest) {
        ExtractAsset.ctx = ctx;
        new ExtractAsset().execute(file, dest);
    }

    static String[] createArgs(String appArgs) {
        ArrayList<String> a = new ArrayList<>(Arrays.asList(appArgs.split(" ")));

        Iterator<String> iter = a.iterator();
        while (iter.hasNext()) {
            if (iter.next().contentEquals("")) {
                iter.remove();
            }
        }

        return a.toArray(new String[0]);
    }


    public static void expand(final View v) {
        v.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    static public String getLogCat() {
        String[] logcatArgs = new String[]{"logcat", "-d", "-v", "time"};

        Process logcatProc = null;
        try {
            logcatProc = Runtime.getRuntime().exec(logcatArgs);
        } catch (IOException e) {
            return null;
        }

        BufferedReader reader = null;
        String response = null;
        try {
            String separator = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), BUFFER_SIZE);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(separator);
            }
            response = sb.toString();
        } catch (IOException ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }

        return response;
    }

    static void copyAsset(Context ctx, String file, String destdir) {
        AssetManager assetManager = ctx.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open(file);
            out = new FileOutputStream(destdir + "/" + file);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + file);
            e.printStackTrace();
        }
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    static void loadArgs(Context ctx, ArrayList<String> args) {
        File cacheDir = ctx.getFilesDir();

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(new File(cacheDir, "args_hist.dat"));
            in = new ObjectInputStream(fis);
            ArrayList<String> argsHistory = (ArrayList<String>) in.readObject();
            args.clear();
            args.addAll(argsHistory);
            in.close();
            return;
        } catch (IOException ignored) {

        } catch (ClassNotFoundException ignored) {

        }
        //failed load, load default
        args.clear();
    }

    static void saveArgs(Context ctx, ArrayList<String> args) {
        File cacheDir = ctx.getFilesDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(new File(cacheDir, "args_hist.dat"));
            out = new ObjectOutputStream(fos);
            out.writeObject(args);
            out.close();
        } catch (IOException ex) {
            Toast.makeText(ctx, "Error saving args History list: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void setImmersionMode(final Activity act) {

        if (AppSettings.immersionMode) {
            act.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            View decorView = act.getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            Log.d(LOG, "onSystemUiVisibilityChange");

                            act.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                        }
                    });
        }
    }

    public static void onWindowFocusChanged(final Activity act, final boolean hasFocus) {

        if (AppSettings.immersionMode) {
            Handler handler = new Handler();

            handler.postDelayed(() -> {

                if (hasFocus) {
                    act.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }, 2000);
        }
    }

    static ArrayList<ActionInput> getGameGamepadConfig(Resources res) {
        ArrayList<ActionInput> actions = new ArrayList<>();

        actions.add(new ActionInput("analog_look_pitch", res.getString(R.string.look_up_down_option), ControlConfig.ACTION_ANALOG_PITCH, Type.ANALOG));
        actions.add(new ActionInput("analog_look_yaw", res.getString(R.string.look_left_right_option), ControlConfig.ACTION_ANALOG_YAW, Type.ANALOG));
        actions.add(new ActionInput("analog_move_fwd", res.getString(R.string.forward_back_option), ControlConfig.ACTION_ANALOG_FWD, Type.ANALOG));
        actions.add(new ActionInput("analog_move_strafe", res.getString(R.string.strafe_option), ControlConfig.ACTION_ANALOG_STRAFE, Type.ANALOG));
        actions.add(new ActionInput("attack", res.getString(R.string.attack_option), ControlConfig.PORT_ACT_ATTACK, Type.BUTTON));
        actions.add(new ActionInput("attack_alt", res.getString(R.string.attack_alt_option), ControlConfig.PORT_ACT_ALT_ATTACK, Type.BUTTON));
        actions.add(new ActionInput("back", res.getString(R.string.move_back_option), ControlConfig.PORT_ACT_BACK, Type.BUTTON));
        actions.add(new ActionInput("crouch", res.getString(R.string.crouch_option), ControlConfig.PORT_ACT_DOWN, Type.BUTTON));
        actions.add(new ActionInput("custom_0", res.getString(R.string.custom_0_option), ControlConfig.PORT_ACT_CUSTOM_0, Type.BUTTON));
        actions.add(new ActionInput("custom_1", res.getString(R.string.custom_1_option), ControlConfig.PORT_ACT_CUSTOM_1, Type.BUTTON));
        actions.add(new ActionInput("custom_2", res.getString(R.string.custom_2_option), ControlConfig.PORT_ACT_CUSTOM_2, Type.BUTTON));
        actions.add(new ActionInput("custom_3", res.getString(R.string.custom_3_option), ControlConfig.PORT_ACT_CUSTOM_3, Type.BUTTON));
        actions.add(new ActionInput("custom_4", res.getString(R.string.custom_4_option), ControlConfig.PORT_ACT_CUSTOM_4, Type.BUTTON));
        actions.add(new ActionInput("custom_5", res.getString(R.string.custom_5_option), ControlConfig.PORT_ACT_CUSTOM_5, Type.BUTTON));
        actions.add(new ActionInput("fly_down", res.getString(R.string.fly_down_option), ControlConfig.PORT_ACT_FLY_DOWN, Type.BUTTON));
        actions.add(new ActionInput("fly_up", res.getString(R.string.fly_up_option), ControlConfig.PORT_ACT_FLY_UP, Type.BUTTON));
        actions.add(new ActionInput("fwd", res.getString(R.string.fwd_option), ControlConfig.PORT_ACT_FWD, Type.BUTTON));
        actions.add(new ActionInput("inv_drop", res.getString(R.string.inv_drop_option), ControlConfig.PORT_ACT_INVDROP, Type.BUTTON));
        actions.add(new ActionInput("inv_next", res.getString(R.string.inv_next_option), ControlConfig.PORT_ACT_INVNEXT, Type.BUTTON));
        actions.add(new ActionInput("inv_prev", res.getString(R.string.inv_prev_option), ControlConfig.PORT_ACT_INVPREV, Type.BUTTON));
        actions.add(new ActionInput("inv_use", res.getString(R.string.inv_use_option), ControlConfig.PORT_ACT_INVUSE, Type.BUTTON));
        actions.add(new ActionInput("jump", res.getString(R.string.jump_option), ControlConfig.PORT_ACT_JUMP, Type.BUTTON));
        actions.add(new ActionInput("left", res.getString(R.string.left_option), ControlConfig.PORT_ACT_MOVE_LEFT, Type.BUTTON));
        actions.add(new ActionInput("look_left", res.getString(R.string.look_left_option), ControlConfig.PORT_ACT_LEFT, Type.BUTTON));
        actions.add(new ActionInput("look_right", res.getString(R.string.look_right_option), ControlConfig.PORT_ACT_RIGHT, Type.BUTTON));
        actions.add(new ActionInput("map_down", res.getString(R.string.map_down_option), ControlConfig.PORT_ACT_MAP_DOWN, Type.BUTTON));
        actions.add(new ActionInput("map_left", res.getString(R.string.map_left_option), ControlConfig.PORT_ACT_MAP_LEFT, Type.BUTTON));
        actions.add(new ActionInput("map_right", res.getString(R.string.map_right_option), ControlConfig.PORT_ACT_MAP_RIGHT, Type.BUTTON));
        actions.add(new ActionInput("map_show", res.getString(R.string.map_show_option), ControlConfig.PORT_ACT_MAP, Type.BUTTON));
        actions.add(new ActionInput("map_up", res.getString(R.string.map_up_option), ControlConfig.PORT_ACT_MAP_UP, Type.BUTTON));
        actions.add(new ActionInput("map_zoomin", res.getString(R.string.map_zoomin_option), ControlConfig.PORT_ACT_MAP_ZOOM_IN, Type.BUTTON));
        actions.add(new ActionInput("map_zoomout", res.getString(R.string.map_zoomout_option), ControlConfig.PORT_ACT_MAP_ZOOM_OUT, Type.BUTTON));
        actions.add(new ActionInput("menu_back", res.getString(R.string.menu_back_option), ControlConfig.MENU_BACK, Type.MENU));
        actions.add(new ActionInput("menu_down", res.getString(R.string.menu_down_option), ControlConfig.MENU_DOWN, Type.MENU));
        actions.add(new ActionInput("menu_left", res.getString(R.string.menu_left_option), ControlConfig.MENU_LEFT, Type.MENU));
        actions.add(new ActionInput("menu_right", res.getString(R.string.menu_right_option), ControlConfig.MENU_RIGHT, Type.MENU));
        actions.add(new ActionInput("menu_select", res.getString(R.string.menu_select_option), ControlConfig.MENU_SELECT, Type.MENU));
        actions.add(new ActionInput("menu_up", res.getString(R.string.menu_up_option), ControlConfig.MENU_UP, Type.MENU));
        actions.add(new ActionInput("next_weapon", res.getString(R.string.next_weapon_option), ControlConfig.PORT_ACT_NEXT_WEP, Type.BUTTON));
        actions.add(new ActionInput("prev_weapon", res.getString(R.string.prev_weapon_option), ControlConfig.PORT_ACT_PREV_WEP, Type.BUTTON));
        actions.add(new ActionInput("quick_load", res.getString(R.string.quick_load_option), ControlConfig.PORT_ACT_QUICKLOAD, Type.BUTTON));
        actions.add(new ActionInput("quick_save", res.getString(R.string.quick_save_option), ControlConfig.PORT_ACT_QUICKSAVE, Type.BUTTON));
        actions.add(new ActionInput("right", res.getString(R.string.right_option), ControlConfig.PORT_ACT_MOVE_RIGHT, Type.BUTTON));
        actions.add(new ActionInput("show_keys", res.getString(R.string.show_keys_option), ControlConfig.PORT_ACT_SHOW_KEYS, Type.BUTTON));
        actions.add(new ActionInput("show_weap", res.getString(R.string.show_weap_option), ControlConfig.PORT_ACT_SHOW_WEAPONS, Type.BUTTON));
        actions.add(new ActionInput("speed", res.getString(R.string.speed_option), ControlConfig.PORT_ACT_SPEED, Type.BUTTON));
        actions.add(new ActionInput("strafe_on", res.getString(R.string.strafe_on_option), ControlConfig.PORT_ACT_STRAFE, Type.BUTTON));
        actions.add(new ActionInput("use", res.getString(R.string.use_option), ControlConfig.PORT_ACT_USE, Type.BUTTON));

        return actions;
    }

    static private class ExtractAsset extends AsyncTask<String, Integer, Long> {

        static Context ctx;
        String errorstring = null;
        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(ctx);
            progressBar.setMessage("Extracting files..");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        int getTotalZipSize(String file) {
            int ret = 0;
            try {
                ZipFile zf = new ZipFile(file);
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry) e.nextElement();
                    String name = ze.getName();

                    ret += ze.getSize();
                    long compressedSize = ze.getCompressedSize();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return ret;
        }

        protected Long doInBackground(String... info) {

            String file = info[0];
            String basePath = info[1];

            boolean isLocal = false;

            progressBar.setProgress(0);

            try {

                BufferedInputStream in = null;
                FileOutputStream fout = null;


                AssetManager assetManager = ctx.getAssets();
                InputStream ins = assetManager.open(file);

                progressBar.setMax(1024 * 1024 * 5); //TODO FIX ME

                in = new BufferedInputStream(ins);

                if (file.endsWith(".zip")) {
                    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.isDirectory()) {
                            // Assume directories are stored parents first then children.
                            System.err.println("Extracting directory: " + entry.getName());
                            // This is not robust, just for demonstration purposes.
                            (new File(basePath, entry.getName())).mkdirs();
                            continue;
                        }
                        Log.d(LOG, "Extracting file: " + entry.getName());
                        (new File(basePath, entry.getName())).getParentFile().mkdirs();
                        BufferedInputStream zin = new BufferedInputStream(zis);
                        OutputStream out = new FileOutputStream(new File(basePath, entry.getName()));
                        Utils.copyFile(zin, out, progressBar);
                    }
                } else {
                    File outZipFile = new File(basePath, "temp.zip");

                    fout = new FileOutputStream(outZipFile);
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        fout.write(data, 0, count);
                        progressBar.setProgress(progressBar.getProgress() + count);
                    }
                    in.close();
                    fout.close();

                    outZipFile.renameTo(new File(basePath, file));
                    return 0L;
                }

            } catch (IOException e) {
                errorstring = e.toString();
                return 1L;
            }

            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            progressBar.dismiss();
            if (errorstring != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage("Error accessing server: " + errorstring)
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id) -> {
                        });

                builder.show();
            }
        }
    }
}
