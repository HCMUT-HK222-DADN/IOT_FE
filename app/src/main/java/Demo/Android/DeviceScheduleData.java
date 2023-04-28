package Demo.Android;

import org.json.JSONObject;

public class DeviceScheduleData {
    private String deviceType;
    private int value;
    private String timeStart;
    public DeviceScheduleData(JSONObject jsonObject) {
        this.deviceType = jsonObject.optString("Device");
        this.value = jsonObject.optInt("Value");
        this.timeStart = jsonObject.optString("TimeStart");
    }

    public DeviceScheduleData(String deviceType, int value, String timeStart) {
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
