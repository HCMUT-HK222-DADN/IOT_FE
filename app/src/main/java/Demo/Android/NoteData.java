package Demo.Android;

import org.json.JSONObject;

public class  NoteData {
    private String deviceType;
    private int value;
    private String timeStart;
    public NoteData(JSONObject jsonObject) {
        this.deviceType = jsonObject.optString("Censor");
        this.value = jsonObject.optInt("Value");
        this.timeStart = jsonObject.optString("TimeStart");
    }

    public NoteData(String deviceType, int value, String timeStart) {
        this.deviceType = deviceType;
        this.value = value;
        this.timeStart = timeStart;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getValue() {
        return value;
    }

    public String getTimeStart() {
        return timeStart;
    }
}
