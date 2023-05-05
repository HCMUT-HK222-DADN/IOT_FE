package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkingScheduleActivity extends AppCompatActivityExtended {
    Button logout, startSchedule, addSchedule, deleteSchedule;
    ListView list_view;
    List<WorkingScheduleData> dataList;
    WorkingScheduleData selectedItem;
    private int selectedPosition;
    WorkingScheduleAdapter adapter;
    private WebSocketManager webSocketManager;
    MeowBottomNavigation bottomNavigation;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workingschedule);

        // ---------------- Create object to handle button
        logout = (Button) findViewById(R.id.logout);
        startSchedule = findViewById(R.id.startSchedule);
        addSchedule = findViewById(R.id.addSchedule);
        deleteSchedule = findViewById(R.id.deleteSchedule);
        list_view = findViewById(R.id.list_view);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(WorkingScheduleActivity.this);
        webSocketManager.start();

        // ---------------- Set up Bottom Bar
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(WorkingScheduleActivity.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
        bottomNavigation.show(ID_SETTING,true);

        // ---------------- Init View
        dataList = new ArrayList<>();
        dataList.add(new WorkingScheduleData("2025-05-25 20:00:00", "2025-05-25 22:00:00",
                                            "00:25:00", "00:05:00"));
        adapter = new WorkingScheduleAdapter(this, dataList);
        list_view.setAdapter(adapter);
        list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // ---------------- Request the list from the server
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", "RequestSchedule");
            jsonObject.put("UserID", 1);
            this.webSocketManager.sendMessage(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ---------------- Init listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        startSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedWorkingScheduleData = selectedItem;
                gotoWorkingSession();
            }
        });
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAssignSession();
            }
        });
        deleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "RequestScheduleDelete");
                    jsonObject.put("UserID", 1);
                    jsonObject.put("Position", selectedPosition);
                    sendMessage(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                gotoWorkingScheduleActivity();
            }
        });
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedItem = adapter.getItem(position);
                selectedPosition = position;
                Log.w("WorkingScheduleActivity", "The position is: " + String.valueOf(selectedPosition));
            }
        });
    }
    //  ---------------- Addition Method
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
        List<WorkingScheduleData> newDataList = new ArrayList<>();
        for (int i = 0; i < jsonObjectList.size(); i++) {
            newDataList.add(new WorkingScheduleData(jsonObjectList.get(i)));
        }
        // Reset the view
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataList.clear();
                dataList.addAll(newDataList);
                adapter.notifyDataSetChanged();
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
    public void gotoWorkingSession() {
        Intent intent = new Intent(this, WorkingSessionActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
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
    public void gotoAssignSession() {
        Intent intent = new Intent(this, AssignSession.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingScheduleActivity() {
        Log.w("WorkingActivity", "in function gotoWorkingSchedule");
        Intent intent = new Intent(this, WorkingScheduleActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}
