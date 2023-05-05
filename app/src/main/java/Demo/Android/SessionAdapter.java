package Demo.Android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SessionAdapter extends ArrayAdapter<SessionData> {

    private List<SessionData> mDataList;
    private Context mContext;

    // --------------------------- Constructor
    // Input the json file to create the adapter for the view_list
    public SessionAdapter(Context context, List<SessionData> dataList) {
        // Call the constructor of the parent class, and give additional info
        super(context, R.layout.activity_sessionitem, dataList);
        this.mContext = context;
        this.mDataList = dataList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_sessionitem, parent, false);
        }
        // Get the ID of the view
        TextView text1 = convertView.findViewById(R.id.text1);
        TextView text2 = convertView.findViewById(R.id.text2);
        // Get the data
        SessionData data = mDataList.get(position);
        // Set data into view
        text1.setText(data.getType());
        text2.setText(data.getInterval());
        return convertView;
    }
}

