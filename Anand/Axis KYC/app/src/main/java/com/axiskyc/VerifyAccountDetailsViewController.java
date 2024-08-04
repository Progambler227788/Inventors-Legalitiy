package com.axiskyc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.axiskyc.custom.Global;
import com.axiskyc.custom.apis.WebService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VerifyAccountDetailsViewController extends BaseActivity {

    private EditText txt_user_id, txt_password, txt_customer_id;
    private Button btn_login;
    
    private Calendar mCalendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account_details);

        Global.controllerName = "VerifyAccountDetailsViewController";
        
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_user_id = (EditText) findViewById(R.id.txt_user_id);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_customer_id = (EditText) findViewById(R.id.txt_customer_id);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        txt_customer_id.setText(Global.user_id);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String full_name = txt_user_id.getText().toString();
                String dob = txt_password.getText().toString();
                String customer_id = txt_customer_id.getText().toString();

                 if (full_name.equalsIgnoreCase("") || full_name.equalsIgnoreCase("null")) {
                    Toast.makeText(VerifyAccountDetailsViewController.this, "Full name required", Toast.LENGTH_LONG).show();
                } else if (dob.equalsIgnoreCase("") || dob.equalsIgnoreCase("null")) {
                    Toast.makeText(VerifyAccountDetailsViewController.this, "Date of birth required", Toast.LENGTH_LONG).show();
                } else if (customer_id.equalsIgnoreCase("") || customer_id.equalsIgnoreCase("null")) {
                    Toast.makeText(VerifyAccountDetailsViewController.this, "Customer ID required", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        Global.webservice_file_name = "thirdPageData.php";

                        JSONObject obj = new JSONObject();
                        obj.put("fullname", full_name);
                        obj.put("dob", dob);

                        obj.put("mobile", Global.user_id);
                        obj.put("customerid", customer_id);

                        new WebService(VerifyAccountDetailsViewController.this, "", new WebService.myCallback() {
                            @Override
                            public void onServiceResponse(String responseJson) {
                                Global.ShowLogs("responseJson = " + responseJson);
                                try {

                                    Global.user_details.put("full_name", full_name);
                                    Global.user_details.put("dob", dob);

                                    Global.StoreFileToAppCache(VerifyAccountDetailsViewController.this, Global.user_details.toString(), "user_details");
                                    Global.StoreFileToAppCache(VerifyAccountDetailsViewController.this, Global.user_id, "user_id");

                                    Intent in = new Intent();
                                    in.setClass(VerifyAccountDetailsViewController.this, MainViewController.class);
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

        txt_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog date_picker = new DatePickerDialog(VerifyAccountDetailsViewController.this, Global.dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mCalendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat displayDateFormat = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            displayDateFormat = new SimpleDateFormat("dd/MM/YYYY");
                        }
                        displayDateFormat.setCalendar(mCalendar);
                        String date_time = displayDateFormat.format(mCalendar.getTime());

                        Global.ShowLogs("display_date = " + date_time);
                        txt_password.setText(date_time);
                    }

                }, mYear, mMonth, mDay);
                date_picker.setTitle("Date of Birth");
                date_picker.show();
            }
        });
    }
}