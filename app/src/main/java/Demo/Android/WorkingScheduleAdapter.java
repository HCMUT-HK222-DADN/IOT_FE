package Demo.Android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

public class WorkingScheduleAdapter extends ArrayAdapter<WorkingScheduleData> {

    private List<WorkingScheduleData> mDataList;
    private Context mContext;

    // --------------------------- Constructor
    // Input the json file to create the adapter for the view_list
    public WorkingScheduleAdapter(Context context, List<WorkingScheduleData> dataList) {
        // Call the constructor of the parent class, and give additional info
        super(context, R.layout.activity_workingscheduleitem, dataList);
        this.mContext = context;
        this.mDataList = dataList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_workingscheduleitem, parent, false);
        }
        // Get the ID of the view
        TextView text1 = convertView.findViewById(R.id.text1);
        TextView text2 = convertView.findViewById(R.id.text2);
        TextView text3 = convertView.findViewById(R.id.text3);
        TextView text4 = convertView.findViewById(R.id.text4);
        // Get the data
        WorkingScheduleData data = mDataList.get(position);
        // Set data into view
        text1.setText(data.getTimeStart());
        text2.setText(data.getTimeEnd());
        text3.setText(data.getWorkInter());
        text4.setText(data.getRestInter());
        return convertView;
    }
}

