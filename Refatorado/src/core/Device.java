package core;

public interface Device {
    String getId();
    void on();
    void off();
    boolean isOn();
}