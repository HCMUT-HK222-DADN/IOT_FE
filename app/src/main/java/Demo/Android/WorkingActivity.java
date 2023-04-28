package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
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
    Button logout, workingschedule, deviceschedule, deviceassign, home;
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working);
        // ---------------- Create object to handle button
        motion = findViewById(R.id.motiondetect);
        txtTemp = findViewById(R.id.Temperature);
        txtHumi = findViewById(R.id.Humidity);
        txtLight = findViewById(R.id.light);
        AssignSession = findViewById(R.id.AssignSession);
        logout = (Button) findViewById(R.id.logout);
        workingschedule = findViewById(R.id.workingschedule);
        deviceschedule = findViewById(R.id.deviceschedule);
        deviceassign = findViewById(R.id.deviceassign);
        home = findViewById(R.id.home_button);

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
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoMainActivity3();
            }
        });
        workingschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWorkingSchedule();
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
        });    }
    //  ---------------- Addition Method
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void gotoWorkingSchedule() {
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
}
