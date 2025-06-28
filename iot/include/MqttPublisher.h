#pragma once
#include "SensorData.h"
#include <mqtt/async_client.h>
#include <string>
#include <memory>

class MqttPublisher {
public:
    MqttPublisher(const std::string& brokerUri,
                  const std::string& topic,
                  int  qos = 1);

    void publish(const SensorData& data);

private:
    std::string _topic;
    std::unique_ptr<mqtt::async_client> _client;
};
