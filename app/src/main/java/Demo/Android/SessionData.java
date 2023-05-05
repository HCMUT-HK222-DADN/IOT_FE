package Demo.Android;

import org.json.JSONObject;

public class SessionData {
    private String type, interval;
    public SessionData(String type, String interval) {
        this.type = type;
        this.interval = interval;
    }
    public SessionData(JSONObject jsonObject) {
        this.type = jsonObject.optString("type");
        this.interval = jsonObject.optString("interval");
    }

    public String getType() {
        return type;
    }
    public String getInterval() {
        return interval;
    }
}
