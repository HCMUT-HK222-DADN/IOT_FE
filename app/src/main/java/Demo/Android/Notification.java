package Demo.Android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Notification extends AppCompatActivityExtended {
    private WebSocketManager webSocketManager;
    Button test;
    private static final int NOTIFICATION_ID = 1;

    private final int ID_HOME = 1;
    private final int ID_ACCOUNT = 2;
    private final int ID_NOTE = 3;
    private final int ID_SETTING = 4;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    List<NoteData> NoteList;
    NoteAdapter selectedItem;
    private int selectedPosition;
    NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        webSocketManager = new WebSocketManager(Notification.this);
        webSocketManager.start();
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.baseline_home_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTE,R.drawable.baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING,R.drawable.baseline_settings_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener(){
            @Override
            public void onClickItem(MeowBottomNavigation.Model item){
                Toast.makeText(Notification.this,"Click item : "+item.getId(),Toast.LENGTH_SHORT).show();
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
        bottomNavigation.setCount(ID_NOTE,"4");
        bottomNavigation.show(ID_NOTE,true);

        // Notification Test
        test = findViewById(R.id.Test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification(Notification.this,"Warning Temperature Overload","100*c");
            }
        });
        ListView list = (ListView) findViewById(R.id.listViewNote);

        NoteList = new ArrayList<>();
        NoteList.add(new NoteData("Temperature", 100, getToday()));
        adapter = new NoteAdapter(this, NoteList);
        list.setAdapter(adapter);

    }
    public void sendNotification(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if running on Android Oreo or higher to create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.baseline_key_24)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show notification
        if(notificationManager!=null)
            notificationManager.notify(getNotificationId(), builder.build());

    }
    public String getToday(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month = month + 1 ;
        return String.valueOf(year) + "/" + String.valueOf(day) +"/"+String.valueOf(month);
    }
    public int getNotificationId(){
        return (int) new Date().getTime();
    }
    public void moveToMain3(){
        Intent intent = new Intent(this, MainActivity3.class);
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
    public void moveToSetting(){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
}