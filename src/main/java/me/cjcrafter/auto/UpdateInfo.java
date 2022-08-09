package me.cjcrafter.auto;

public class UpdateInfo {

    public final Version current;
    public final Version newest;

    public UpdateInfo(Version current, Version newest) {
        this.current = current;
        this.newest = newest;
    }
}
