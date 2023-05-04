package Demo.Android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivityExtended  {
    private WebSocketManager webSocketManager;
    private Button Login_button;
    private TextView serverStatus, username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------------- Create Websocket
        webSocketManager = new WebSocketManager(MainActivity.this);
        webSocketManager.start();

        // ---------------------------- Create buttons and views
        username = (TextView) findViewById(R.id.username);
        username.setText("");
        password = (TextView) findViewById(R.id.password);
        password.setText("");
        Login_button = (Button) findViewById(R.id.Button_login);
        serverStatus = findViewById(R.id.serverStatus);

        // ---------------------------- Create new websocket


        // ---------------------------- Create listener
        Login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputUsername = username.getText().toString();
                String inputPassword = password.getText().toString();
                Log.w("MainActivity", "The input Username is:" + inputUsername);
                Log.w("MainActivity", "The input Password is:" + inputPassword);
                if (inputUsername.isEmpty() & inputPassword.isEmpty()) {
                    gotoManActivity3();
                } else if (!inputUsername.isEmpty() & !inputPassword.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Type", "RequestLogIn");
                        jsonObject.put("Username", inputUsername);
                        jsonObject.put("Password", inputPassword);
                        sendMessage(jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    // ---------------------------- Additional method
    public void sendMessage(JSONObject jsonObject) {
        this.webSocketManager.sendMessage(jsonObject);
    }
    public void login(JSONObject jsonObject) {
        this.userID = jsonObject.optInt("UserID");
        gotoManActivity3();
    }
    public void gotoManActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
        webSocketManager.closeSocket();
        finish();
    }
    public void updateServerStatus(String noti) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverStatus.setText(noti);
            }
        });
    }
}
