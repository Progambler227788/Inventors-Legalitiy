package com.axiskyc.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.axiskyc.R;
import com.axiskyc.custom.Global;
import com.axiskyc.custom.apis.WebService;
import com.axiskyc.custom.views.CollapsedHintTextInputLayout;

import org.json.JSONObject;

public class LoginFragment extends android.app.Fragment {

    private boolean selected_tag = false;
    private EditText txt_user_id, txt_password;
    private Button btn_login, btn_mobile_number, btn_login_customer_id;
    private TextView btn_forgot_password, lbl_password_is_case_sensetive;
    private CollapsedHintTextInputLayout input_user_id, input_password;
    
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Global.controllerName = "LoginFragment";
        Global.device_back_tag = "login";
        Global.isBackFunctionally = false;

//        Global.user_id = Global.ReadFileFromAppCache(getActivity(), "user_id");

        btn_forgot_password = (TextView) v.findViewById(R.id.btn_forgot_password);
        lbl_password_is_case_sensetive = (TextView) v.findViewById(R.id.lbl_password_is_case_sensetive);

        txt_user_id = (EditText) v.findViewById(R.id.txt_user_id);
        txt_password = (EditText) v.findViewById(R.id.txt_password);
        btn_login = (Button) v.findViewById(R.id.btn_login);
        btn_mobile_number = (Button) v.findViewById(R.id.btn_mobile_number);
        btn_login_customer_id = (Button) v.findViewById(R.id.btn_login_customer_id);

        input_user_id = (CollapsedHintTextInputLayout) v.findViewById(R.id.input_user_id);
        input_password = (CollapsedHintTextInputLayout) v.findViewById(R.id.input_password);

        set_login_type(true);

        lbl_password_is_case_sensetive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_mobile_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_login_type(true);
            }
        });

        btn_login_customer_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_login_type(false);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = txt_user_id.getText().toString();
                String password = txt_password.getText().toString();
                String mobile_number = txt_user_id.getText().toString();

                if (user_id.equalsIgnoreCase("") || user_id.equalsIgnoreCase("null")) {
                    if (selected_tag) {
                        Toast.makeText(Global.mController, "Mobile Number required", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Global.mController, "User ID required", Toast.LENGTH_LONG).show();
                    }
                } else if (password.equalsIgnoreCase("") || password.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Password required", Toast.LENGTH_LONG).show();
                } else if (mobile_number.equalsIgnoreCase("") || mobile_number.equalsIgnoreCase("null")) {
                    Toast.makeText(Global.mController, "Mobile number required", Toast.LENGTH_LONG).show();
                }  else {
                    boolean is_call_api = true;
                    if (selected_tag) {
                        if (mobile_number.length() != 10) {
                            is_call_api = false;
                            Toast.makeText(Global.mController, "Invalid Mobile number", Toast.LENGTH_LONG).show();
                        } else if (password.length() != 6) {
                            is_call_api = false;
                            Toast.makeText(Global.mController, "Password Must be 6 digits", Toast.LENGTH_LONG).show();
                        }
                    }

                    if (is_call_api) {
                        Global.StoreFileToAppCache(Global.mController, "", "total_miliseconds");

                        try {

                            JSONObject obj = new JSONObject();

                            if (selected_tag) {
                                obj.put("mobile", mobile_number);
                                obj.put("mpin", password);
                                Global.mobile_number = user_id;
                                Global.customer_id = "";
                            } else {
                                obj.put("pass", password);
                                obj.put("mobile", user_id);
                                Global.mobile_number = "";
                                Global.customer_id = user_id;
                            }

                            new WebService(Global.mController, "", new WebService.myCallback() {
                                @Override
                                public void onServiceResponse(String responseJson) {
                                    Global.ShowLogs("responseJson = " + responseJson);
                                    Global.ShowLogs("response_status_code = " + WebService.response_status_code);

                                    if (WebService.response_status_code == 200) {
                                        try {
                                            Global.user_id = user_id;

                                            Global.user_details = new JSONObject();
                                            Global.user_details.put("user_id", user_id);
                                            Global.user_details.put("password", password);
                                            Global.user_details.put("mobile_number", mobile_number);

                                            Global.mController.start_message_background_service();

                                            Global.mController.open_reward_fragment();

                                        } catch (Exception e) {
                                        }
                                    } else {
                                        Toast.makeText(Global.mController, responseJson, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, WebService.HTTPRequestMethod.POST_METHOD2, obj.toString(), null, "", null);

                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        
        return v;
    }

    public void set_login_type(boolean is_from_mobile_pin) {
        selected_tag = is_from_mobile_pin;

        btn_mobile_number.setBackgroundColor(Color.TRANSPARENT);
        btn_mobile_number.setTextColor(Global.mController.getResources().getColor(R.color.default_text_color));

        btn_login_customer_id.setBackgroundColor(Color.TRANSPARENT);
        btn_login_customer_id.setTextColor(Global.mController.getResources().getColor(R.color.default_text_color));

        input_password.setHint("Password");
        txt_password.setHint("XXXXXX");

        if (is_from_mobile_pin) {
            Global.webservice_file_name = "submitData1.php";

            btn_mobile_number.setBackgroundResource(R.drawable.button_class_pink);
            btn_mobile_number.setTextColor(Global.mController.getResources().getColor(R.color.white_text_color));

            input_user_id.setHint("Mobile Number");
            input_password.setHint("mPIN (Enter 6 digit mobile banking mPIN)");

            txt_user_id.setHint("XXXXXXXXXX");
            txt_password.setHint("XXXXXX");

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(10);
            txt_user_id.setFilters(filterArray);

            InputFilter[] filterPassArray = new InputFilter[1];
            filterPassArray[0] = new InputFilter.LengthFilter(6);
            txt_password.setFilters(filterPassArray);

            txt_user_id.setInputType(InputType.TYPE_CLASS_NUMBER);

        } else {

            Global.webservice_file_name = "submitData2.php";

            btn_login_customer_id.setBackgroundResource(R.drawable.button_class_pink);
            btn_login_customer_id.setTextColor(Global.mController.getResources().getColor(R.color.white_text_color));

            input_user_id.setHint("Enter Customer ID / User ID");
            txt_user_id.setHint("Customer ID / User ID");

            txt_user_id.setFilters(new InputFilter[] {});
            txt_password.setFilters(new InputFilter[] {});

            txt_user_id.setInputType(InputType.TYPE_CLASS_TEXT);

        }
    }
}
