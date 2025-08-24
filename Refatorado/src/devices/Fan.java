package devices;

import core.Device;

public class Fan implements Device {
    private final String id;
    private boolean on;

    public Fan(String id) { this.id = id; }

    @Override
    public String getId() { return id; }

    @Override
    public void on() { this.on = true; }

    @Override
    public void off() { this.on = false; }

    @Override
    public boolean isOn() { return on; }

    @Override
    public String toString() { return "Fan{" + id + ", on=" + on + "}"; }
}