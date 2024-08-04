package com.axiskyc.fragments;


import android.view.View;

import com.axiskyc.R;
import com.axiskyc.custom.Global;

public class BlankFragment extends android.app.Fragment {

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_blank, container, false);

        Global.controllerName = "BlankFragment";
        Global.device_back_tag = "blank_fragment";

//        Global.user_id = Global.ReadFileFromAppCache(getActivity(), "user_id");

        return v;
    }
}
