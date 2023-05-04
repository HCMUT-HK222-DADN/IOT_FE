package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.DayNightSwitch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import java.nio.charset.Charset;

public class WorkingActivity extends AppCompatActivityExtended {
    TextView txtTemp, txtHumi, txtLight, motion,AssignSession;
    Button logout, workingschedule, deviceschedule, deviceassign, home, workingSession;
    private WebSocketManager webSocketManager;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working);
        Log.w("WorkingActivity", "into WorkingActivity");

        // ---------------- Create object to handle button
        motion = findViewById(R.id.motiondetect);
        txtTemp = findViewById(R.id.Temperature);
        txtHumi = findViewById(R.id.Humidity);
        txtLight = findViewById(R.id.light);
        AssignSession = findViewById(R.id.AssignSession);
        logout = findViewById(R.id.logout);
        workingschedule = findViewById(R.id.workingschedule);
        workingSession = findViewById(R.id.workingSession);
        deviceschedule = findViewById(R.id.deviceschedule);
        deviceassign = findViewById(R.id.deviceassign);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(WorkingActivity.this);
        webSocketManager.start();

        // ---------------- Init 4 sensor value
        this.initSensorValue();

        // ---------------- Init listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        workingschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("WorkingActivity", "workingSchedule botton cliked");
                gotoWorkingScheduleActivity();
            }
        });
        deviceschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoDeviceSchedule();
            }
        });
        deviceassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAssignDevice();
            }
        });
        AssignSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSessionWorkingSchedule();
            }
        });
        workingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWorkingSessionActivity();
            }
        });
        //NavBar
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(WorkingActivity.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()){
                    case ID_HOME:
                        name = "home";
                        moveToMain3();
                        break;
                    case ID_ACCOUNT:
                        name = "account";
                        moveToAccount();
                        break;
                    case ID_NOTE:
                        name = "notification";
                        moveToANotification();
                        break;
                    case ID_SETTING:
                        name = "setting";
                        moveToSetting();
                        break;
                    default:
                        name="Current";
                        break;
                }
            }
        });


        //Dùng chức năng này phát triển module 2 - Cảnh báo giá trị vượt ngưỡng
        bottomNavigation.setCount(ID_NOTE,"4");
    }
    //  ---------------- Addition Method
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
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
    public void gotoMainActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoDeviceSchedule() {
        Intent intent = new Intent(this, DeviceScheduleActivity.class);
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
    public void gotoSessionWorkingSchedule() {
        Intent intent = new Intent(this, AssignSession.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingSessionActivity() {
        Intent intent = new Intent(this, WorkingSessionActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public  void moveToMain3(){
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToSetting(){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToAccount(){
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToANotification(){
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void initSensorValue() {
        this.webSocketManager.sendMessage("RequestUpdateSensor");
    }
    @Override
    public void updateSensorValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("WebSocket", "Activity Received JSON File success.");
                String tempValue = jsonObject.optString("Temp");
                String humiValue = jsonObject.optString("Humi");
                String lightValue = jsonObject.optString("Light");
                int motionValue = jsonObject.optInt("Motion");
                txtTemp.setText(tempValue);
                txtHumi.setText(humiValue);
                txtLight.setText(lightValue);
                if (motionValue == 1) {
                    motion.setText("Detected");
                } else {
                    motion.setText("None");
                }
            }
        });
    }
    public void updateHumiValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("MainActivity3", "Activity Received JSON File success.");
                String humiValue = jsonObject.optString("Humi");
                txtHumi.setText(humiValue + "%");
            }
        });
    }
    public void updateLightValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("MainActivity3", "Activity Received JSON File success.");
                String lightValue = jsonObject.optString("Light");
                txtLight.setText(lightValue + "lux");
            }
        });
    }
    public void updateTempValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("MainActivity3", "Activity Received JSON File success.");
                String tempValue = jsonObject.optString("Temp");
                txtTemp.setText(tempValue + "°C");
            }
        });
    }
    public void updateMotionValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("MainActivity3", "Activity Received JSON File success.");
                int motionValue = jsonObject.optInt("Motion");
                if (motionValue == 1) {
                    motion.setText("Detected");
                } else {
                    motion.setText("None");
                }
            }
        });
    }
}
