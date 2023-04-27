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
import org.json.JSONObject;
import java.nio.charset.Charset;

public class MainActivity3 extends AppCompatActivityExtended {
    MQTTHelper mqttHelper;


    TextView txtTemp,txtHumi,txtLight,tView,motion;
    SeekBar sBar;
    Button logout, tempgraph, humigraph, lightgraph, btnWorking;
    DayNightSwitch btnLight ;
    //DbTemp TempHelper;
    //DbHumi HumiHelper;
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
        // HumiHelper = new DbHumi(this);
        // TempHelper = new DbTemp(this);

        // ---------------- Receive Websocket object
        webSocketManager = new WebSocketManager(MainActivity3.this);
        webSocketManager.start();

        // ---------------- Init 4 sensor value
        this.initSensorValue();


        // txtHumi.setText(String.valueOf(HumiHelper.getLastYValue())+"%");
        // txtTemp.setText(String.valueOf(TempHelper.getLastYValue()) + "°C");
        //Set up Bottom Bar
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
                        moveToWorkingActivity();

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
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tView.setText(pval + "/" + seekBar.getMax());
                String s = Integer.toString(pval);
                sendDataMQTT("LamVinh/feeds/fan\n",s);

            }
        });
        //Quay ve trang dang nhap
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        btnWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToWorkingActivity();
            }
        });
//        tempgraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TempGraph();
//            }
//        });
//        humigraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                HumiGraph();
//            }
//        });
//        lightgraph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LightGraph();
//            }
//        });
        btnLight.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("LamVinh/feeds/button1\n", "1");
                }
                else{
                    sendDataMQTT("LamVinh/feeds/button1\n", "0");
                }
            }
        });
        // Now using websocket, so we comment out MQTT
        // startMQTT();
    }
    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);
        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void startMQTT(){

        mqttHelper = new MQTTHelper(this    );
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST",topic + "---" + message.toString());

                if(topic.contains("humi-info")){
                    txtHumi.setText(message.toString()+"  %");
                }
                else if(topic.contains("temp-info")){
                    txtTemp.setText(message.toString()+"  °C");
                }
                else if(topic.contains("light2")){
                    txtLight.setText(message.toString()+"   lux");
                }
                else if(topic.contains("motion")){
                    if(message.toString().equals("1")){
                        motion.setText("Detected");
                    }
                    else
                        motion.setText("None");

                }
                else if(topic.contains("button1")){
                    if(message.toString().equals("1")){
                        btnLight.setOn(true);
                    }
                    else btnLight.setOn(false);
                }
                else if(topic.contains("fan")){
                    sBar.setProgress(Integer.parseInt(message.toString()));
                    tView.setText((message.toString())+"/" + sBar.getMax());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    //  ---------------- Addition Method
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void moveToWorkingActivity(){
        Intent intent = new Intent(this, WorkingActivity.class);
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
//    public void TempGraph() {
//        Intent intent = new Intent(this, TempGraph.class);
//        startActivity(intent);
//    }
//    public void HumiGraph() {
//        Intent intent = new Intent(this, HumiGraph.class);
//        startActivity(intent);
//    }
//    public void LightGraph() {
//        Intent intent = new Intent(this, Light_graph.class);
//        startActivity(intent);
//    }
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
