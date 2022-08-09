package me.cjcrafter.auto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Supplier;

public class UpdateChecker {

    private final Version current;
    private final Supplier<Version> versionSupplier;

    public UpdateChecker(Plugin plugin, Supplier<Version> versionSupplier) {
        this.current = new Version(plugin.getDescription().getVersion());
        this.versionSupplier = versionSupplier;
    }

    public UpdateChecker(Version current, Supplier<Version> versionSupplier) {
        this.current = current;
        this.versionSupplier = versionSupplier;
    }

    public boolean hasUpdate() {
        Version newest = versionSupplier.get();
        return Version.isOutOfDate(current, newest);
    }

    public static Supplier<Version> github(String organization, String repo) {
        return github("https://api.github.com/repos/" + organization + "/" + repo + "/releases/latest");
    }

    public static Supplier<Version> github(String link) {
        return () -> {
            try {
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(30 * 1000);

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String version = json.get("tag_name").getAsString();

                return new Version(version);

            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        };
    }

    public static Supplier<Version> spigot(int id, String name) {
        return spigot("https://api.spigotmc.org/legacy/update.php?resource=" + id, name);
    }

    public static Supplier<Version> spigot(String link, String name) {
        return () -> {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("User-Agent", name);
                String version = (new BufferedReader(new InputStreamReader(connection.getInputStream()))).readLine();

                return new Version(version);

            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        };
    }
}
