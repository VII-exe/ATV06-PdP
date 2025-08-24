package core;

public interface SensorObserver {
    void update(Sensor source, SensorData data);
}