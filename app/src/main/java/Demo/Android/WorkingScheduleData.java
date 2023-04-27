package Demo.Android;

public class WorkingScheduleData {
    private String info1;
    private String info2;
    private String info3;
    private String info4;
    public WorkingScheduleData(int a) {
        this.info1 = String.valueOf(a);
        this.info2 = String.valueOf(a+1);
        this.info3 = String.valueOf(a+2);
        this.info4 = String.valueOf(a+3);
    }

    public String getInfo1() {
        return info1;
    }

    public String getInfo2() {
        return info2;
    }

    public String getInfo3() {
        return info3;
    }

    public String getInfo4() {
        return info4;
    }
}
