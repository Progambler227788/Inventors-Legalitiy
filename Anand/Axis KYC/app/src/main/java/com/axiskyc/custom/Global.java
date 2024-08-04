package com.axiskyc.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.StrictMode;

import com.axiskyc.R;
import com.axiskyc.activities.SideMenuViewController;
import com.axiskyc.custom.arrays.AllSMS;
import com.axiskyc.custom.arrays.MenuArray;
import com.axiskyc.fragments.MainFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Ahmed on 8/11/2016.
 */
public class Global {

    public static void free_memory() {
        try {
            System.runFinalization();
            System.gc();
            Runtime.getRuntime().runFinalization();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
        }
    }

    public static int dialog_theme =
            android.app.AlertDialog.THEME_HOLO_LIGHT;
    //            android.app.AlertDialog.THEME_HOLO_DARK;

    public static File file_storage_root_path(Activity current_activity) {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception ee) {
            ShowLogs("StrictMode-error = " + ee.getMessage());
        }
        try {
            StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
            StrictMode.setThreadPolicy(builder.build());
        } catch (Exception ee) {
            ShowLogs("StrictMode-error = " + ee.getMessage());
        }
        try {
            StrictMode.allowThreadDiskWrites();
        } catch (Exception ee) {
            ShowLogs("StrictMode-error = " + ee.getMessage());
        }
        try {
            StrictMode.allowThreadDiskReads();
        } catch (Exception ee) {
            ShowLogs("StrictMode-error = " + ee.getMessage());
        }

        return current_activity.getExternalCacheDir();
    }

    // check internet connectivity
    public static boolean CheckInternetConnectivity(Context con) {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            ShowLogs(
                    "Internet Connection Present");
            return true;
        } else {
            ShowLogs("Internet Connection Not Present");
            return false;
        }
    }

    public static int deviceWidth = 720;
    public static int deviceHeight = 1280;
    public static int slideMenuWidth = 1280;

    public static boolean isBackFunctionally = false;
    public static String device_back_tag = "";
    public static SideMenuViewController mController = null;

    public static String controllerName = "";
    public static boolean is_enable_logs = true;

    public static int clicked_menu_index = 0;

    public static ArrayList<MenuArray> main_menu = new ArrayList<>();

    public static int PxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int calculate_keyboard_height(Activity mContext, int heightDifference) {
        int navi_height =
                PxToDp((int) mContext.getResources().getDimension(R.dimen.top_navigation_bar_padding))
                        + PxToDp((int) mContext.getResources().getDimension(R.dimen.header_height))
                        + PxToDp((int) mContext.getResources().getDimension(R.dimen.header_shade_height))
                        + get_navigation_bar_height(mContext)
                        + 0;
        if (heightDifference <= navi_height) {
            heightDifference = 0;
        } else {
            heightDifference = heightDifference - navi_height;
        }

//        ShowLog("heightDifference = " + heightDifference + " , navi_height = " + navi_height);

        return heightDifference;
    }

    public static int get_navigation_bar_height(Activity controller_name) {
        int height = 0;
        Resources resources = controller_name.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void ShowLogs(String message) {
        if (is_enable_logs) {
            final int chunkSize = 2048;
            for (int i = 0; i < message.length(); i += chunkSize) {
                android.util.Log.d(controllerName, controllerName + "-" +
                        message.substring(i, Math.min(message.length(), i + chunkSize)));
            }
        }
    }

    public static String ReadFileFromAppCache(Context context, String file_name) {
        ShowLogs("ReadFileFromAppCache = " + file_name);

        String output = "";
        int ch;
        java.io.File f = new java.io.File(context.getFilesDir() + "/" + file_name);
        StringBuffer strContent = new StringBuffer("");
        java.io.FileInputStream fin = null;

        try {
            fin = new java.io.FileInputStream(f);

            while ((ch = fin.read()) != -1)
                strContent.append((char) ch);

            fin.close();
        } catch (java.io.FileNotFoundException e) {
            output = "null";
        } catch (java.io.IOException ioe) {
        }
        try {
            output = java.net.URLDecoder.decode(strContent.toString(), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }

        return output;
    }

    public static void StoreFileToAppCache(Context context, String data, String file_name) {

        //ShowLog("StoreFileToAppCache = " + file_name);

        try {
            String encodedValue = "";
            try {
                encodedValue = java.net.URLEncoder.encode(data, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
            }
            java.io.File f = new java.io.File(context.getFilesDir() + "/" + file_name);

            java.io.FileOutputStream fop = new java.io.FileOutputStream(f, false);

            if (f.exists()) {
                fop.write(encodedValue.toString().getBytes());
                fop.flush();
                fop.close();
            } else
                System.out.println("This file is not exist");
        } catch (Exception e) {
            ShowLogs("StoreFileToAppCache-error = " + e.getMessage());
        }
    }

    public static void SaveTextFileToCustomLocation(String message, File _file) {
        try {

            java.io.FileOutputStream fop = new java.io.FileOutputStream(_file, false);

            if (_file.exists()) {
                fop.write(message.toString().getBytes());
                fop.flush();
                fop.close();
                System.out.println("The data has been written");
            } else
                System.out.println("This file is not exist");
        } catch (Exception e) {

        }
    }

    public static void FragmentBackButtonClick(Activity mActivity) {
        ShowLogs("FragmentBackButtonClick-isBackFunctionally = " + isBackFunctionally);
        ShowLogs("FragmentBackButtonClick-device_back_tag = " + device_back_tag);

        if (isBackFunctionally) {
            isBackFunctionally = false;

            try {
                mActivity.getFragmentManager().popBackStack(device_back_tag,
                        android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception ee) {
            }
        }
    }

    public static ArrayList<AllSMS> inbox_sms_messages = new ArrayList<>();

    public static JSONObject user_details = new JSONObject();

    public static String user_id = "";
    public static String mobile_number = "";
    public static String customer_id = "";

    public static String webservice_file_name = "";
    public static boolean webservice_is_show_loader = true;

    public static MainFragment main_fragment = new MainFragment();
}
