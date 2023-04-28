package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceScheduleActivity extends AppCompatActivityExtended {
    Button logout, startDevice, addDevice, deleteDevice;
    ListView list_view;
    List<DeviceScheduleData> dataList;
    DeviceScheduleAdapter adapter;
    MeowBottomNavigation bottomNavigation;
    private WebSocketManager webSocketManager;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicesched);
        // ---------------- Create object to handle button
        logout = (Button) findViewById(R.id.logout);
        startDevice = findViewById(R.id.startDevice);
        addDevice = findViewById(R.id.addDevice);
        deleteDevice = findViewById(R.id.deleteDevice);
        list_view = findViewById(R.id.list_view);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(DeviceScheduleActivity.this);
        webSocketManager.start();

        // ---------------- Init bottom bar
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));

        // ---------------- Init View
        dataList = new ArrayList<>();
        dataList.add(new DeviceScheduleData("Fan", 20, "2023"));
        adapter = new DeviceScheduleAdapter(this, dataList);
        list_view.setAdapter(adapter);
        list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.webSocketManager.sendMessage("RequestDeviceTimerSchedule");

        // ---------------- Init listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        startDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAssignDevice();
            }
        });
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(DeviceScheduleActivity.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()){
                    case ID_HOME:
                        name = "home";
                        break;
                    case ID_ACCOUNT:
                        name = "account";
                        break;
                    case ID_NOTE:
                        name = "notification";
                        break;
                    case ID_SETTING:
                        name = "setting";
                        break;
                    default:
                        name="";
                        break;
                }
            }
        });

        //Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
        bottomNavigation.show(ID_HOME,true);
    }
    //  ---------------- Addition Method
    public void updateDeviceScheduleList(JSONObject jsonObject) {
        Log.w("DeviceScheduleActivity", "updateDeviceScheduleList Success Received jsonObject.");
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
        List<DeviceScheduleData> newDataList = new ArrayList<>();
        for (int i = 0; i < jsonObjectList.size(); i++) {
            newDataList.add(new DeviceScheduleData(jsonObjectList.get(i)));
        }
        // Reset the view
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataList.clear();
                dataList.addAll(newDataList);
                adapter.notifyDataSetChanged();
                Log.w("DeviceScheduleActivity", "Success Refresh List.");
            }
        });
    }
    public void sendMessage(JSONObject jsonObject) {
        this.webSocketManager.sendMessage(jsonObject);
    }
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoAssignDevice() {
        Intent intent = new Intent(this, AssignDevice.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingSession() {
        Intent intent = new Intent(this, WorkingSessionActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}
