package com.smartroom.strategy;

import com.smartroom.report.SensorData;

public interface ActionStrategy {
    void apply(SensorData data);
}
