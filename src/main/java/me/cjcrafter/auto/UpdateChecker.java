package me.cjcrafter.auto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Supplier;

public class UpdateChecker implements Listener {

    private final Version current;
    private final Supplier<Version> versionSupplier;

    private Version cache;

    public UpdateChecker(Plugin plugin, Supplier<Version> versionSupplier) {
        this(new Version(plugin.getDescription().getVersion()), versionSupplier);
    }

    public UpdateChecker(Version current, Supplier<Version> versionSupplier) {
        this.current = current;
        this.versionSupplier = versionSupplier;
        hasUpdate(true);
    }

    public UpdateInfo hasUpdate() {
        return hasUpdate(false);
    }

    /**
     * Returns an {@link UpdateInfo} if there is an update available, else
     * returns <code>null</code>. Since this method uses https connections,
     * this method should only be run async.
     *
     * @param forceUpdate true to force web lookup, false uses cache.
     * @return The {@link UpdateInfo}, or <code>null</code>.
     */
    public UpdateInfo hasUpdate(boolean forceUpdate) {
        if (!forceUpdate && cache != null && Version.isOutOfDate(current, cache))
            return new UpdateInfo(current, cache);

        Version newest = versionSupplier.get();
        cache = newest;
        boolean hasUpdate = Version.isOutOfDate(current, newest);
        return hasUpdate ? new UpdateInfo(current, newest) : null;
    }

    public static Supplier<Version> github(String organization, String repo) {
        return github("https://api.github.com/repos/" + organization + "/" + repo + "/releases/latest");
    }

    public static Supplier<Version> github(String link) {
        return () -> {
            try {
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
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
