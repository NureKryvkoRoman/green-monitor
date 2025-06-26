package ua.nure.kryvko.greenmonitor.plant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;

import java.util.List;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "plant")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToMany(mappedBy = "plant")
    List<Greenhouse> greenhouses;

    @NotBlank
    String name;

    @NotNull
    Float minTemperature;

    @NotNull
    Float maxTemperature;

    @NotNull
    Float minHumidity;

    @NotNull
    Float maxHumidity;

    @NotNull
    Float minMoisture;

    @NotNull
    Float maxMoisture;

    public Plant() {}

    public Plant(Integer id, List<Greenhouse> greenhouses, String name, Float minTemperature,
                 Float maxTemperature, Float minHumidity, Float maxHumidity, Float minMoisture, Float maxMoisture) {
        this.id = id;
        this.greenhouses = greenhouses;
        this.name = name;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minMoisture = minMoisture;
        this.maxMoisture = maxMoisture;
    }

    public boolean isNormalTemperature(Float temperature) {
        return temperature > minTemperature && temperature < maxTemperature;
    }

    public boolean isNormalHumidity(Float humidity) {
        return humidity > minHumidity && humidity < maxHumidity;
    }

    public boolean isNormalMoisture(Float moisture) {
        return moisture > minMoisture && moisture < maxMoisture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Greenhouse> getGreenhouses() {
        return greenhouses;
    }

    public void setGreenhouses(List<Greenhouse> greenhouses) {
        this.greenhouses = greenhouses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Float getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(Float minHumidity) {
        this.minHumidity = minHumidity;
    }

    public Float getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Float maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Float getMinMoisture() {
        return minMoisture;
    }

    public void setMinMoisture(Float minMoisture) {
        this.minMoisture = minMoisture;
    }

    public Float getMaxMoisture() {
        return maxMoisture;
    }

    public void setMaxMoisture(Float maxMoisture) {
        this.maxMoisture = maxMoisture;
    }
}
