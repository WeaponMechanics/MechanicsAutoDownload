package me.cjcrafter.auto;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

    private final int major;
    private final int minor;
    private final int patch;

    public Version(String str) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find())
            throw new IllegalArgumentException("Couldn't find x.x.x format in '" + str + "'");

        String result = matcher.group();
        String[] split = result.split("\\.");

        major = Integer.parseInt(split[0]);
        minor = Integer.parseInt(split[1]);
        patch = Integer.parseInt(split[2]);
    }

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public static boolean isOutOfDate(Version current, Version newest) {
        // 1.2.3
        // 1.3.1
        if (newest.major > current.major)
            return true;
        if (newest.minor > current.minor)
            return true;
        if (newest.patch > current.patch)
            return true;

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
