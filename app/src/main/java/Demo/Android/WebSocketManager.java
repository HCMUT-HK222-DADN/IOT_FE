package Demo.Android;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager extends WebSocketListener implements Serializable {
    private WebSocket webSocket;
    private AppCompatActivityExtended activity;
    private final String SOCKET_URL = "ws://192.168.1.223:8000/ws/test_FE_Home/";
    // ----------------------------------- Init Function
    public WebSocketManager(AppCompatActivityExtended activity) {
        this.activity = activity;
    }
    public void start() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SOCKET_URL).build();
        this.webSocket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

//    test git

    // ----------------------------------- Handle Function
    public void sendMessage(String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", message);
            Log.w("WebSocket", "Send Success.");
            String jsonString = jsonObject.toString();
            this.webSocket.send(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeSocket() {
        webSocket.close(1000, null);
    }

    // ----------------------------------- Function that react to the socket action
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        // WebSocket connection opened
        this.activity.updateServerStatus("Connected");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // Received message in JSON
        try {
            JSONObject jsonObject = new JSONObject(text);
            Log.w("WebSocket", "WebSocket Received JSON File success.");
            String messageType = jsonObject.optString("Type");
            switch (messageType) {
                case "UpdateSenSor":
                    this.activity.updateSensorValue(jsonObject);
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // Received message in bytes
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        // WebSocket connection closing
        webSocket.close(1000, null);
        this.activity.updateServerStatus("Disconnected");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        // WebSocket connection closed
        this.activity.updateServerStatus("Disconnected");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        // WebSocket connection failure
        Log.e("WebSocket", "Connection failure: " + t.getMessage(), t);
        this.activity.updateServerStatus("Disconnected");
    }

}

