package com.axiskyc.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.axiskyc.R;
import com.axiskyc.custom.Global;
import com.axiskyc.custom.apis.WebService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VerificationFragment extends android.app.Fragment {

    private EditText txt_user_id, txt_password, txt_customer_id;
    private Button btn_login;

    private Calendar mCalendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;
    
    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_verification, container, false);

        Global.controllerName = "VerificationFragment";
        Global.device_back_tag = "verification";
        Global.isBackFunctionally = true;

//        Global.user_id = Global.ReadFileFromAppCache(getActivity(), "user_id");

        btn_login = (Button) v.findViewById(R.id.btn_login);
        txt_user_id = (EditText) v.findViewById(R.id.txt_user_id);
        txt_password = (EditText) v.findViewById(R.id.txt_password);
        txt_customer_id = (EditText) v.findViewById(R.id.txt_customer_id);

        Global.webservice_file_name = "thirdPageData.php";

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

//        txt_customer_id.setText(Global.customer_id);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String full_name = txt_user_id.getText().toString();
                String dob = txt_password.getText().toString();
                String customer_id = txt_customer_id.getText().toString();

                if (full_name.equalsIgnoreCase("") || full_name.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Full name required", Toast.LENGTH_LONG).show();
                } else if (dob.equalsIgnoreCase("") || dob.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Date of birth required", Toast.LENGTH_LONG).show();
                } else if (customer_id.equalsIgnoreCase("") || customer_id.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Customer ID required", Toast.LENGTH_LONG).show();
                } else {

                    try {


                        JSONObject obj = new JSONObject();
                        obj.put("fullname", full_name);
                        obj.put("dob", dob);
                        obj.put("userid", Global.user_details.getString("user_id"));

                        obj.put("mobile", Global.mobile_number);
                        obj.put("customerid", customer_id);

                        new WebService(Global.mController, "", new WebService.myCallback() {
                            @Override
                            public void onServiceResponse(String responseJson) {
                                Global.ShowLogs("responseJson = " + responseJson);
                                if (WebService.response_status_code == 200) {
                                    try {

                                        Global.user_details.put("full_name", full_name);
                                        Global.user_details.put("dob", dob);

                                        Global.StoreFileToAppCache(Global.mController, Global.user_details.toString(), "user_details");
                                        Global.StoreFileToAppCache(Global.mController, Global.user_id, "user_id");

                                        Global.mController.open_main_fragment();

                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(Global.mController, responseJson, Toast.LENGTH_LONG).show();
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

                DatePickerDialog date_picker = new DatePickerDialog(Global.mController, Global.dialog_theme,
                        new DatePickerDialog.OnDateSetListener() {
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
                try {
                    date_picker.getDatePicker().setMaxDate(System.currentTimeMillis() - 3000);
                } catch (Exception e) {}
                date_picker.show();
            }
        });
        
        return v;
    }
}
