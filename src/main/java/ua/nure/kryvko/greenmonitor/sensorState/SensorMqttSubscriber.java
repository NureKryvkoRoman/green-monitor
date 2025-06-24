package ua.nure.kryvko.greenmonitor.sensorState;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;

import java.util.Date;
import java.util.UUID;

@Component
public class SensorMqttSubscriber {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MqttProperties mqttProperties;

    @Autowired
    private SensorStateService sensorStateService;

    @PostConstruct
    public void init() {
        try {
            MqttClient client = new MqttClient(
                    mqttProperties.getBrokerUrl(),
                    mqttProperties.getClientId() != null ? mqttProperties.getClientId() : UUID.randomUUID().toString()
            );

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);

            client.subscribe(mqttProperties.getTopic(), this::handleMessage);
        } catch (MqttException e) {
            throw new RuntimeException("Failed to connect to MQTT broker", e);
        }
    }

    private void handleMessage(String topic, MqttMessage mqttMessage) {
        try {
            String payload = new String(mqttMessage.getPayload());

            MqttSensorPayload data = objectMapper.readValue(payload, MqttSensorPayload.class);

            SensorState state = new SensorState();
            state.setTimestamp(new Date());
            state.setTemperature(data.getTemperature());
            state.setHumidity(data.getHumidity());
            state.setMoisture(data.getMoisture());

            Sensor sensor = new Sensor();
            sensor.setId(data.getSensorId());
            state.setSensor(sensor);

            sensorStateService.saveSensorState(state);

        } catch (Exception e) {
            System.err.println("Error processing MQTT message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class MqttSensorPayload {
        private Integer sensorId;
        private Float temperature;
        private Float humidity;
        private Float moisture;

        public MqttSensorPayload() {
        }

        public MqttSensorPayload(Integer sensorId, Float temperature, Float humidity, Float moisture) {
            this.sensorId = sensorId;
            this.temperature = temperature;
            this.humidity = humidity;
            this.moisture = moisture;
        }

        public Integer getSensorId() {
            return sensorId;
        }

        public void setSensorId(Integer sensorId) {
            this.sensorId = sensorId;
        }

        public Float getTemperature() {
            return temperature;
        }

        public void setTemperature(Float temperature) {
            this.temperature = temperature;
        }

        public Float getHumidity() {
            return humidity;
        }

        public void setHumidity(Float humidity) {
            this.humidity = humidity;
        }

        public Float getMoisture() {
            return moisture;
        }

        public void setMoisture(Float moisture) {
            this.moisture = moisture;
        }
    }
}
