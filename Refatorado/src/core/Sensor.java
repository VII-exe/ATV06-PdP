package core;

import report.SensorData;

public interface Sensor {
    String getId();
    SensorData read();
    void addObserver(SensorObserver observer);
    void removeObserver(SensorObserver observer);
    void notifyObservers(SensorData data);
}