package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkingScheduleActivity extends AppCompatActivityExtended {
    Button logout, startSchedule;
    ListView list_view;
    private WebSocketManager webSocketManager;
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
        list_view = findViewById(R.id.list_view);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(WorkingScheduleActivity.this);
        webSocketManager.start();

        // ---------------- Init View
        List<WorkingScheduleData> dataList = new ArrayList<>();
        dataList.add(new WorkingScheduleData(1));
        dataList.add(new WorkingScheduleData(2));
        dataList.add(new WorkingScheduleData(3));
        dataList.add(new WorkingScheduleData(4));
        WorkingScheduleAdapter adapter = new WorkingScheduleAdapter(this, dataList);
        list_view.setAdapter(adapter);
        list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


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
                gotoWorkingSession();
            }
        });
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(WorkingScheduleActivity.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
}
