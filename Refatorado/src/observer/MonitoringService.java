package com.smartroom.observer;

import com.smartroom.core.Sensor;
import com.smartroom.core.SensorObserver;
import com.smartroom.report.SensorData;
import com.smartroom.strategy.ActionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonitoringService implements SensorObserver {
    private final List<SensorData> history = new ArrayList<>();
    private final List<ActionStrategy> strategies = new ArrayList<>();

    public void registerStrategy(ActionStrategy strategy) {
        this.strategies.add(strategy);
    }

    public List<SensorData> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public void update(Sensor source, SensorData data) {
        history.add(data);
        for (ActionStrategy s : strategies) {
            s.apply(data);
        }
    }
}
