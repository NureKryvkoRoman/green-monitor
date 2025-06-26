package ua.nure.kryvko.greenmonitor.sensor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.sensorState.SensorState;

import java.util.List;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "sensor")
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "greenhouse_id", referencedColumnName = "id")
    Greenhouse greenhouse;

    @OneToMany(mappedBy = "sensor")
    List<SensorState> sensorStates;

    String note;

    public Sensor() {}

    public Sensor(Integer id, Greenhouse greenhouse, String note, List<SensorState> sensorStates) {
        this.id = id;
        this.greenhouse = greenhouse;
        this.note = note;
        this.sensorStates = sensorStates;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<SensorState> getSensorStates() {
        return sensorStates;
    }

    public void setSensorStates(List<SensorState> sensorStates) {
        this.sensorStates = sensorStates;
    }
}
