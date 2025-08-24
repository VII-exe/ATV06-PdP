package strategy;

import report.SensorData;

public interface ActionStrategy {
    void apply(SensorData data);
}