package ua.nure.kryvko.greenmonitor.sensor;

public class SensorResponse {
    Integer id;
    Integer greenhouseId;
    String note;

    public SensorResponse() {}

    public SensorResponse(Integer id, Integer greenhouseId, String note) {
        this.id = id;
        this.greenhouseId = greenhouseId;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(Integer greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
