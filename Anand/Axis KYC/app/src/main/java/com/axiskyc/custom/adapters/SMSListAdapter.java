package com.axiskyc.custom.adapters;

import android.widget.TextView;

import com.axiskyc.R;
import com.axiskyc.custom.arrays.AllSMS;

public class SMSListAdapter extends android.widget.BaseAdapter {

    private android.app.Activity activity;
    private java.util.ArrayList<AllSMS> data;
    private static android.view.LayoutInflater inflater = null;

    public SMSListAdapter(android.app.Activity a, java.util.ArrayList<AllSMS> d) {
        activity = a;
        data = d;
        inflater = (android.view.LayoutInflater) activity
                .getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView txt_sms_data;
    }

    public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {

        android.view.View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.row_sms, null);
                holder = new ViewHolder();

                holder.txt_sms_data = (TextView) vi.findViewById(R.id.txt_sms_data);

                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();

            holder.txt_sms_data.setText(data.get(position).body);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return vi;
    }
}