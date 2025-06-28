#include "SensorData.h"
#include "Dht22Reader.h"
#include "MqttPublisher.h"
#include <nlohmann/json.hpp>
#include <iostream>
#include <thread>

int main(int argc, char* argv[])
{
    if (argc < 2) {
        std::cerr << "Usage: " << argv[0] << " (dht22 <gpio>|json)\n";
        return 1;
    }

    constexpr char BROKER_URI[] = "tcp://localhost:1883";
    constexpr char TOPIC[]      = "sensors/state";

    MqttPublisher publisher(BROKER_URI, TOPIC);

    std::string mode = argv[1];

    if (mode == "dht22") {
        if (argc < 3) {
            std::cerr << "Need GPIO pin for DHT22 mode\n";
            return 1;
        }
        unsigned pin = static_cast<unsigned>(std::stoi(argv[2]));
        Dht22Reader reader(pin);

        SensorData data;
        data.sensorId = 1;

        while (reader.read(data)) {
            publisher.publish(data);
            std::this_thread::sleep_for(std::chrono::seconds(2));
        }
    }
    else if (mode == "json") {
        std::string input((std::istreambuf_iterator<char>(std::cin)),
                          std::istreambuf_iterator<char>());

        if (input.empty()) {
            std::cerr << "No JSON provided on stdin\n";
            return 1;
        }

        auto j = nlohmann::json::parse(input);
        SensorData data;
        data.sensorId    = j.value("sensorId", 0);
        data.temperature = j.value("temperature", 0.0f);
        data.humidity    = j.value("humidity", 0.0f);
        data.moisture    = j.value("moisture", 0.0f);

        publisher.publish(data);
    }
    else {
        std::cerr << "Unknown mode " << mode << "\n";
        return 1;
    }

    return 0;
}
