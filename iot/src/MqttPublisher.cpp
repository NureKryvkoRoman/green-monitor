#include "MqttPublisher.h"
#include "SensorData.h"
#include <nlohmann/json.hpp>
#include <mqtt/async_client.h>
#include <iostream>
#include <memory>

MqttPublisher::MqttPublisher(const std::string& brokerUri,
                             const std::string& topic,
                             int qos)
    : _topic(topic)
{
    const std::string clientId = "sensorPublisher_" + std::to_string(std::rand());
    _client = std::make_unique<mqtt::async_client>(brokerUri, clientId);

    mqtt::connect_options opts;
    opts.set_automatic_reconnect(true);
    _client->connect(opts)->wait();
    std::cout << "Connected to " << brokerUri << "\n";
}

void MqttPublisher::publish(const SensorData& data)
{
    nlohmann::json j = {
        {"sensorId",    data.sensorId},
        {"temperature", data.temperature},
        {"humidity",    data.humidity},
        {"moisture",    data.moisture}
    };

    mqtt::message_ptr msg = mqtt::make_message(_topic, j.dump());
    msg->set_qos(1);
    _client->publish(msg);
    std::cout << "Sent: " << j.dump() << "\n";
}
