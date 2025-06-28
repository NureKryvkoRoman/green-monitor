cmake_minimum_required(VERSION 3.15)
project(sensor_mqtt_demo LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)

find_package(PahoMqttCpp REQUIRED)
find_package(PahoMqttC REQUIRED)
find_package(nlohmann_json 3.2.0 REQUIRED)
find_package(PIGPIO REQUIRED)

include_directories(include)

add_executable(
    sensor_mqtt_demo
    src/main.cpp
    src/MqttPublisher.cpp
    src/Dht22Reader.cpp
)

target_link_libraries(
    sensor_mqtt_demo
    PahoMqttCpp::paho-mqttpp3
    PahoMqttC::paho-mqtt3c
    nlohmann_json::nlohmann_json
    pigpio
)

