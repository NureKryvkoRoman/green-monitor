package ua.nure.kryvko.greenmonitor.greenhouse;

public class GreenhouseSummary {
    Integer id;
    Integer userId;
    String name;
    Long sensorCount;
    String plantName;

    public GreenhouseSummary() {}

    public GreenhouseSummary(Integer id, Integer userId, String name, Long sensorCount, String plantName) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.sensorCount = sensorCount;
        this.plantName = plantName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(Long sensorCount) {
        this.sensorCount = sensorCount;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }
}
