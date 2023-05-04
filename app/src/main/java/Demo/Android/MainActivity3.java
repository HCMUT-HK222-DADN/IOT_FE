package Demo.Android;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.DayNightSwitch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivityExtended {
    TextView txtTemp,txtHumi,txtLight,tView,motion,gate;
    SeekBar sBar;
    Button logout, tempgraph, humigraph, lightgraph, btnWorking;
    DayNightSwitch btnLight;
    private WebSocketManager webSocketManager;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // ---------------- Create object to handle button
        motion = findViewById(R.id.motiondetect);
        txtTemp = findViewById(R.id.Temperature);
        txtHumi = findViewById(R.id.Humidity);
        txtLight = findViewById(R.id.light);
        btnLight = findViewById(R.id.lightswitch);
        tView = (TextView) findViewById(R.id.textview1);
        sBar = (SeekBar) findViewById(R.id.seekBar1);
        logout = (Button) findViewById(R.id.logout);
        tempgraph = (Button) findViewById(R.id.temp_button);
        humigraph = (Button) findViewById(R.id.humi_button);
        lightgraph = (Button) findViewById(R.id.light_button);
        btnWorking = (Button) findViewById(R.id.working_button);
        gate = (TextView) findViewById(R.id.gate);

        // ---------------- Receive Websocket object
        webSocketManager = new WebSocketManager(MainActivity3.this);
        webSocketManager.start();

        // ---------------- Init 4 sensor value
        this.initSensorValue();
        this.initDeviceValue();

        // ---------------- Set up Bottom Bar
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(MainActivity3.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
        bottomNavigation.show(ID_HOME,true);

        // ---------------- Set up Listener
        gate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToGateNumPad();
            }
        });
        btnLight.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                int lightDeviceValue;
                if (isOn) {
                    lightDeviceValue = 1;
                } else {
                    lightDeviceValue = 0;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "RequestDeviceControl");
                    jsonObject.put("Device", "Den");
                    jsonObject.put("Value", lightDeviceValue);
                    sendMessage(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress < 10) {
                    seekBar.setProgress(0);
                } else if (progress >= 10 && progress < 30) {
                    seekBar.setProgress(20);
                } else if (progress >= 30 && progress < 50) {
                    seekBar.setProgress(40);
                } else if (progress >= 50 && progress < 70) {
                    seekBar.setProgress(60);
                } else if (progress >= 70 && progress < 90) {
                    seekBar.setProgress(80);
                } else {
                    seekBar.setProgress(100);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //write custom code to on stop using seekBar
                int pval = seekBar.getProgress();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "RequestDeviceControl");
                    jsonObject.put("Device", "Quat");
                    jsonObject.put("Value", pval);
                    sendMessage(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        btnWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWorkingActivity();
            }
        });
    }
    public void moveToGateNumPad(){
        Intent intent = new Intent(this, Gate.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    //  ---------------- Addition Method
    public void sendMessage(JSONObject jsonObject) {
        this.webSocketManager.sendMessage(jsonObject);
    }
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
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
    public void initSensorValue() {
        this.webSocketManager.sendMessage("RequestUpdateSensor");
    }
    public void initDeviceValue() {
        this.webSocketManager.sendMessage("RequestDeviceStatus");
    }
    @Override
    public void updateSensorValue(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("MainActivity3", "Activity Received JSON File success.");
                String tempValue = jsonObject.optString("Temp");
                String humiValue = jsonObject.optString("Humi");
                String lightValue = jsonObject.optString("Light");
                int motionValue = jsonObject.optInt("Motion");
                txtTemp.setText(tempValue + "°C");
                txtHumi.setText(humiValue + "%");
                txtLight.setText(lightValue + "lux");
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
    public void deviceInit(JSONObject jsonObject) {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < jsonObjectList.size(); i++) {
                    String device = jsonObjectList.get(i).optString("Device");
                    int value = jsonObjectList.get(i).optInt("Value");
                    if (device.equals("Den")) {
                        if (value == 1) {
                            btnLight.setOn(true);
                        } else if (value == 0) {
                            btnLight.setOn(false);
                        }
                    } else if (device.equals("Quat")) {
                        sBar.setProgress(value);
                    }
                }
            }
        });
    }
    public void deviceControl(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String device = jsonObject.optString("Device");
                Log.w("MainActivity3", "Device Type: " + device);
                int value = jsonObject.optInt("Value");
                Log.w("MainActivity3", "Value: " + Integer.toString(value));
                if (device.equals("Den")) {
                    if (value == 1) {
                        btnLight.setOn(true);
                    } else if (value == 0) {
                        btnLight.setOn(false);
                    }
                } else if (device.equals("Quat")) {
                    sBar.setProgress(value);
                }
            }
        });
    }
    public void deviceControlAuto(JSONObject jsonObject) {
        deviceControl(jsonObject);
        String device = jsonObject.optString("Device");
        int value = jsonObject.optInt("Value");
        JSONObject jsonObjectNew = new JSONObject();
        try {
            jsonObjectNew.put("Type", "RequestDeviceControl");
            jsonObjectNew.put("Device", device);
            jsonObjectNew.put("Value", value);
            Log.w("MainActivity3", "Device type to be sent: " + device);
            Log.w("MainActivity3", "Value to be sent: " + Integer.toString(value));
            sendMessage(jsonObjectNew);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
}
