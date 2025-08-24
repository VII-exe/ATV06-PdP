package report;

import java.time.Instant;
import java.util.Objects;

public class SensorData {
    private final String sensorId;
    private final String type;
    private final double value;
    private final Instant timestamp;

    public SensorData(String sensorId, String type, double value, Instant timestamp) {
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='"
                + sensorId
                + ", type='"
                + type
                + ", value="
                + value
                + ", timestamp="
                + timestamp
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorData)) return false;
        SensorData that = (SensorData) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(sensorId, that.sensorId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, type, value, timestamp);
    }
}