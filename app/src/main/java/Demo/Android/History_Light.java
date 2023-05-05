package Demo.Android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Light_data {
    private String Date;
    private int value;
    private String censor;


    public Light_data(JSONObject jsonObject) {
        this.Date = jsonObject.optString("Date");
        this.value = jsonObject.optInt("Value");
        this.censor = jsonObject.optString("censor");

    }

    public Light_data(String Date, int value,String censor) {
        this.Date = Date;
        this.value = value;
        this.censor = censor;
    }

    public String getDate() {
        return Date;
    }

    public int getValue() {
        return value;
    }

    public String getcensor() {
        return censor;
    }
}
class LightAdapter extends ArrayAdapter<Light_data> {

    private List<Light_data> mDataList;
    private Context mContext;

    // --------------------------- Constructor
    // Input the json file to create the adapter for the view_list
    public LightAdapter(Context context, List<Light_data> dataList) {
        // Call the constructor of the parent class, and give additional info
        super(context, R.layout.activity_history_light, dataList);
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
        Light_data data = mDataList.get(position);
        // Set data into view
        text1.setText(data.getDate());
        text2.setText(String.valueOf(data.getValue()));
        text3.setText(String.valueOf(data.getcensor()));

        return convertView;
    }
}
public class History_Light extends AppCompatActivityExtended {
    private WebSocketManager webSocketManager;
    List<Light_data> LightList;
    private int selectedPosition;
    LightAdapter adapter;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_light);
        webSocketManager = new WebSocketManager(History_Light.this);
        webSocketManager.start();
        // ---------------- Set up Bottom Bar
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(History_Light.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()){
                    case ID_HOME:
                        name = "home";
                        gotoMainActivity3();
                        break;
                    case ID_ACCOUNT:
                        name = "account";
                        break;
                    case ID_NOTE:
                        name = "notification";
                        gotoNotification();
                        break;
                    case ID_SETTING:
                        name = "setting";
                        gotoWorkingActivity();
                        break;
                    default:
                        name="";
                        break;
                }
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case ID_HOME:
                        gotoMainActivity3();
                        break;
                    case ID_SETTING:
                        gotoWorkingActivity();
                        break;
                    default:
                        break;
                }
            }
        });
        //Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
        //bottomNavigation.show(ID_HOME,true);

        //ListView handle
        ListView list = (ListView) findViewById(R.id.listViewLight);

        LightList = new ArrayList<>();
        LightList.add(new Light_data("Light censor", 100, getToday()));

        adapter = new LightAdapter(this, LightList);
        list.setAdapter(adapter);
    }
    public void gotoMainActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingActivity() {
        Intent intent = new Intent(this, WorkingActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoNotification() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void updateWorkingScheduleList(JSONObject jsonObject) {
        // Take the data from the server
        JSONArray jsonArray;
        List<JSONObject> jsonObjectList = new ArrayList<>();
        try {
            jsonArray = jsonObject.getJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjectList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        List<Light_data> newDataList = new ArrayList<>();
        for (int i = 0; i < jsonObjectList.size(); i++) {
            newDataList.add(new Light_data(jsonObjectList.get(i)));
        }
        // Reset the view
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LightList.clear();
                LightList.addAll(newDataList);
                adapter.notifyDataSetChanged();
            }
        });
    }
    public String getToday(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month = month + 1 ;
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        return String.valueOf(year) + "/" + String.valueOf(day) +"/"+String.valueOf(month) + " - " +String.valueOf(hour)+":"+String.valueOf(min);
    }

}