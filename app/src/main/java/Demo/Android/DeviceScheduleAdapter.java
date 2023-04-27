package Demo.Android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceScheduleAdapter extends ArrayAdapter<DeviceScheduleData> {

    private List<DeviceScheduleData> mDataList;
    private Context mContext;

    // --------------------------- Constructor
    // Input the json file to create the adapter for the view_list
    public DeviceScheduleAdapter(Context context, List<DeviceScheduleData> dataList) {
        // Call the constructor of the parent class, and give additional info
        super(context, R.layout.activity_devicescheditem, dataList);
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_devicescheditem, parent, false);
        }
        // Get the id of the view
        TextView text1 = convertView.findViewById(R.id.text1);
        TextView text2 = convertView.findViewById(R.id.text2);
        TextView text3 = convertView.findViewById(R.id.text3);
        // Get the data
        DeviceScheduleData data = mDataList.get(position);
        // Set data into view
        text1.setText(data.getDeviceType());
        text2.setText(String.valueOf(data.getValue()));
        text3.setText(data.getTimeStart());
        return convertView;
    }
}

