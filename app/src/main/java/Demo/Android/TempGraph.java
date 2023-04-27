package Demo.Android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TempGraph extends AppCompatActivityExtended {
    ArcGauge arcGauge;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    Button back, log, logout;
    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ---------------- Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_graph);

        // ---------------- Create object to handle view elements
        graphView = (GraphView) findViewById(R.id.tempgraph);
        back = (Button) findViewById(R.id.back);
        log = (Button) findViewById(R.id.log);
        logout = findViewById(R.id.logout);

        // ---------------- Create Websocket object
        webSocketManager = new WebSocketManager(TempGraph.this);
        webSocketManager.start();

        // ---------------- Init graph and gauge
        this.initTempGraph();
        this.initGauge();

        // ---------------- Init button Listener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoMainActivity3();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
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
                Toast.makeText(TempGraph.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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

    //  ---------------- Init function
    private void initTempGraph() {
        // --------- Series data for the graph
        // Take x values as array of String
        String[] dateString = {
                "17/04/2023 08:30:00",
                "17/04/2023 08:31:00",
                "17/04/2023 08:32:00",
                "17/04/2023 08:33:00",
                "17/04/2023 08:34:00",
        };
        // Parse x values into Date
        Date[] date = {null, null, null, null, null};
        try {
            for (int i = 0; i < 5; i++) {
                date[i] = sdf.parse(dateString[i]);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Create series
        series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(date[0].getTime(), 20),
                new DataPoint(date[1].getTime(), 35),
                new DataPoint(date[2].getTime(), 15),
                new DataPoint(date[3].getTime(), 37),
                new DataPoint(date[4].getTime(), 40)
        });

        // --------- Format of the graph
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Temperature Value");
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(100);

        long xValues = new Date().getTime();
        graphView.getViewport().setMaxX(xValues);
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double Value,boolean isValueX){
                if(isValueX){
                    return sdf.format(new Date((long)Value));
                }
                else{
                    return super.formatLabel(Value,isValueX);
                }
            }
        });
    }
    private void initGauge() {
        arcGauge = findViewById(R.id.tempgauge);
        Range range1 = new Range();
        range1.setFrom(0.0);
        range1.setTo(15.0);
        range1.setColor(Color.BLACK);

        Range range2 = new Range();
        range2.setFrom(15.0);
        range2.setTo(35.0);
        range2.setColor(Color.GREEN);

        Range range3 = new Range();
        range3.setFrom(35.0);
        range3.setTo(100.0);
        range3.setColor(Color.RED);

        arcGauge.addRange(range1);
        arcGauge.addRange(range2);
        arcGauge.addRange(range3);
        arcGauge.setValue(40);
    }

    //  ---------------- Specific define methods
    public void gotoMainActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void LogOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    @Override
    public void updateSensorValue(JSONObject jsonObject){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double tempValue = jsonObject.optDouble("Temp");
                arcGauge.setValue(tempValue);
            }
        });
    }
}