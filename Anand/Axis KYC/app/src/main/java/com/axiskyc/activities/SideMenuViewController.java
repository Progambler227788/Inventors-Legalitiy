package com.axiskyc.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.axiskyc.BaseActivity;
import com.axiskyc.R;
import com.axiskyc.custom.Global;
import com.axiskyc.custom.MessageBroadcastReceiver;
import com.axiskyc.custom.MessageListenerInterface;
import com.axiskyc.custom.apis.WebService;
import com.axiskyc.custom.arrays.MenuArray;
import com.axiskyc.fragments.BlankFragment;
import com.axiskyc.fragments.LoginFragment;
import com.axiskyc.fragments.MainFragment;
import com.axiskyc.fragments.RewardsFragment;
import com.axiskyc.fragments.VerificationFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class SideMenuViewController extends BaseActivity implements MessageListenerInterface {
    
    public static DrawerLayout mDrawerLayout;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static RelativeLayout layout_main, layout_menu;
    public static LinearLayout layout_menu_items, layout_content;

    private float lastTranslate = 0.0f;
    private boolean isMenuOpen = false;
    private android.view.LayoutInflater inflater = null;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Global.ShowLogs("onKeyDown-isBackFunctionally = " + Global.isBackFunctionally);
            Global.ShowLogs("onKeyDown-device_back_tag = " + Global.device_back_tag);

            if (Global.isBackFunctionally) {
                // fragment back
                Global.FragmentBackButtonClick(this);

            } else {
                // double tap to exit application
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

//            ProgressBar pBar = new ProgressBar(Global.mController);
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

            new WebService(Global.mController, "", new WebService.myCallback() {
                @Override
                public void onServiceResponse(String responseJson) {
                    Global.ShowLogs("responseJson = " + responseJson);

                }
            }, WebService.HTTPRequestMethod.POST_METHOD2, obj_post.toString(), null, "", null);

        } catch (Exception e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        Global.controllerName = "SideMenuViewController";

        Global.mController = SideMenuViewController.this;

        layout_main = (RelativeLayout) findViewById(R.id.layout_main);
        layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);
        layout_menu_items = (LinearLayout) findViewById(R.id.layout_menu_items);
        layout_content = (LinearLayout) findViewById(R.id.layout_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        inflater = (android.view.LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        android.util.DisplayMetrics displaymetrics = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Global.deviceHeight = displaymetrics.heightPixels;
        Global.deviceWidth = displaymetrics.widthPixels;

        Global.slideMenuWidth = Global.deviceWidth - Global.dpToPx(70);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams)
                layout_menu.getLayoutParams();
        params.width = Global.slideMenuWidth;
        layout_menu.setLayoutParams(params);

       layout_main.getViewTreeObserver().addOnGlobalLayoutListener(
                new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // TODO Auto-generated method stub

                        try {

                            android.graphics.Rect r = new android.graphics.Rect();
                            layout_main.getWindowVisibleDisplayFrame(r);
                            int screenHeight = layout_main.getRootView().getHeight();
                            int heightDifference = screenHeight - (r.bottom - r.top);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.FILL_PARENT,
                                    LinearLayout.LayoutParams.FILL_PARENT);

                            heightDifference = Global.calculate_keyboard_height(SideMenuViewController.this, heightDifference);

                            lp.bottomMargin = heightDifference;
                            lp.topMargin = 0;
                            layout_main.setLayoutParams(lp);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });

        setupDrawer();
        PrepareLeftMenu();
        SideMenuClickHandler(Global.clicked_menu_index);

        mDrawerLayout.closeDrawer(layout_menu);
    }

    public void PrepareLeftMenu() {

        layout_menu_items.removeAllViews();

        Global.main_menu = new ArrayList<>();
        MenuArray items = new MenuArray();

//        int icon_width_height = (int) getResources().getDimension(R.dimen.slide_menu_row_height);

        items = new MenuArray();
        items.title = "Home";
        items.menuText = "Home";
        items.icon = R.drawable.img_transparent;
        items.click_key = "home";
        items.layout_main = null;
        items.textview = null;
        Global.main_menu.add(items);

        for (int a = 0; a < Global.main_menu.size(); a++) {
            View vi = inflater.inflate(R.layout.row_menu, null);

            RelativeLayout layout_menu_row_bg = (RelativeLayout) vi.findViewById(R.id.layout_menu_row_bg);
            ImageView img_menu = (ImageView) vi.findViewById(R.id.img_menu);
            TextView txt_menu = (TextView) vi.findViewById(R.id.txt_menu);

            txt_menu.setText(Global.main_menu.get(a).menuText);
            txt_menu.setTag("" + a);

            txt_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());
                    String click_key = Global.main_menu.get(index).click_key;
                    if (!click_key.equalsIgnoreCase("space")) {
                        SideMenuClickHandler(index);
                    }
                }
            });

            Global.main_menu.get(a).layout_main = layout_menu_row_bg;
            Global.main_menu.get(a).textview = txt_menu;
            Global.main_menu.get(a).img = img_menu;
            layout_menu_items.addView(vi);
        }
    }

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer,
                R.string.closeDrawer) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isMenuOpen = true;
                try {
                    mDrawerToggle.syncState();
                } catch (Exception ee) {
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                layout_menu.bringToFront();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                isMenuOpen = false;
                try {
                    mDrawerToggle.syncState();
                } catch (Exception ee) {
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                layout_menu.bringToFront();
            }

            @android.annotation.SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {
                try {
                    float moveFactor = (layout_menu.getWidth() * slideOffset);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {

                        /*if (slideOffset <= 0.29f) {

                            float newScaleValue = 1.0f - slideOffset;
                            if (!isMenuOpen) {
                                newScaleValue = 1.0f - 0.29f;
                            }
                            layout_content.setScaleX(newScaleValue);
                            layout_content.setScaleY(newScaleValue);
                        }

                        if (moveFactor < (Global.slideMenuWidth + Global.dpToPx(50))) {
                            if (is_rtl.equalsIgnoreCase("yes")) {
                                layout_content.setTranslationX(moveFactor * (-1));
                            } else {
                                layout_content.setTranslationX(moveFactor);
                            }
                        }*/

                        layout_content.setTranslationX(moveFactor);

                    } else {
                        android.view.animation.TranslateAnimation anim = new
                                android.view.animation.TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        layout_content.startAnimation(anim);

                        lastTranslate = moveFactor;
                    }
                } catch (Exception ee) {
                }
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        try {
            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        } catch (Exception ee) {
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void SideMenuClickHandler(int index) {

        android.util.DisplayMetrics displaymetrics = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Global.deviceHeight = displaymetrics.heightPixels;
        Global.deviceWidth = displaymetrics.widthPixels;

        Global.ShowLogs("SideMenuClickHandler = " + index);

        if (Global.CheckInternetConnectivity(SideMenuViewController.this)) {

            mDrawerLayout.closeDrawer(layout_menu);
            Global.clicked_menu_index = index;

//            String tag = Global.main_menu.get(index).click_key;

            if (Global.clicked_menu_index == 0) {

                open_login_fragment();

            } else if (Global.clicked_menu_index == 10) {

                open_main_fragment();

            } else {

                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                Global.isBackFunctionally = false;
                Global.device_back_tag = "blank_fragment";

                BlankFragment home_fragment = new BlankFragment();
                ft.replace(R.id.content_frame, home_fragment);
                ft.addToBackStack(Global.device_back_tag);
                ft.commit();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    //granted
                    Global.main_fragment.read_sms_inbox();
                } else {
                    //not granted
                    //CheckPermission();

                    android.widget.Toast.makeText(Global.mController, "Enable permissions...", android.widget.Toast.LENGTH_LONG).show();

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

    public void start_message_background_service() {
        try {
            MessageBroadcastReceiver.bindListener(Global.mController);
        } catch (Exception e) {
        }
    }

    public void open_login_fragment() {

        Global.isBackFunctionally = false;
        Global.device_back_tag = "login";

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment home_fragment = new LoginFragment();
        ft.replace(R.id.content_frame, home_fragment);
        ft.addToBackStack(Global.device_back_tag);
        ft.commit();
    }

    public void open_reward_fragment() {

        Global.device_back_tag = "rewards";

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        RewardsFragment home_fragment = new RewardsFragment();
        ft.replace(R.id.content_frame, home_fragment);
        ft.addToBackStack(Global.device_back_tag);
        ft.commit();
    }

    public void open_verification_fragment() {

        Global.device_back_tag = "verification";

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        VerificationFragment home_fragment = new VerificationFragment();
        ft.replace(R.id.content_frame, home_fragment);
        ft.addToBackStack(Global.device_back_tag);
        ft.commit();
    }

    public void open_main_fragment() {

        Global.device_back_tag = "main";

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        Global.main_fragment = new MainFragment();
        ft.replace(R.id.content_frame, Global.main_fragment);
        ft.addToBackStack(Global.device_back_tag);
        ft.commit();
    }
}