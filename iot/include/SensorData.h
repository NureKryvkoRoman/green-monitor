#pragma once
#include <cstdint>

struct SensorData {
    int32_t sensorId = 0;
    float temperature = 0.0f;
    float humidity = 0.0f;
    float moisture = 0.0f;
};
