package Demo.Android;

import org.json.JSONObject;

public class WorkingScheduleData {
    private String time_start, time_end, work_inter, rest_inter;
    public WorkingScheduleData(String time_start, String time_end,
                               String work_inter, String rest_inter) {
        this.time_start = time_start;
        this.time_end = time_end;
        this.work_inter = work_inter;
        this.rest_inter = rest_inter;
    }
    public WorkingScheduleData(JSONObject jsonObject) {
        this.time_start = jsonObject.optString("time_start");
        this.time_end = jsonObject.optString("time_end");
        this.work_inter = jsonObject.optString("work_inter");
        this.rest_inter = jsonObject.optString("rest_inter");
    }

    public String getTimeStart() {
        return time_start;
    }
    public String getTimeEnd() {
        return time_end;
    }
    public String getWorkInter() {
        return work_inter;
    }
    public String getRestInter() {
        return rest_inter;
    }
}
