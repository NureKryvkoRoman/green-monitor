#include "Dht22Reader.h"
#include "SensorData.h"
#include <pigpio.h>
#include <iostream>
#include <chrono>
#include <thread>

Dht22Reader::Dht22Reader(unsigned gpioPin) : _pin(gpioPin)
{
    if (gpioInitialise() < 0) {
        throw std::runtime_error("pigpio init failed");
    }
}

bool Dht22Reader::read(SensorData& out)
{
    constexpr int MAX_RETRIES = 3;
    float temp = 0, hum = 0;
    for (int i = 0; i < MAX_RETRIES; ++i) {
        int status = gpioDHT22(_pin, &hum, &temp);
        if (!status) {
            out.temperature = temp;
            out.humidity    = hum;
            return true;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(800));
    }
    std::cerr << "DHT22 read failed\n";
    return false;
}
