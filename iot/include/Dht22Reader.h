#pragma once
#include "SensorData.h"

/// Lightweight wrapper around pigpio’s DHT22 sample code.
/// Returns true if a new sample was obtained.
class Dht22Reader {
public:
    /// gpioPin – BCM pin number the DHT22 data pin is attached to
    explicit Dht22Reader(unsigned gpioPin);

    bool read(SensorData& out);

private:
    unsigned _pin;
};
