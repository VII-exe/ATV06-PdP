package core;

import report.SensorData;

public interface SensorObserver {
    void update(Sensor source, SensorData data);
}