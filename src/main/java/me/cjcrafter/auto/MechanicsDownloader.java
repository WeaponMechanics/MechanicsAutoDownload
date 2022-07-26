package me.cjcrafter.auto;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public class MechanicsDownloader {

    private final String plugin;
    private final String link;
    private final String version;

    public MechanicsDownloader(String plugin, String link) {
        this.plugin = plugin;
        this.link = link;
        this.version = null;
    }

    public MechanicsDownloader(String plugin, String link, String version) {
        this.plugin = plugin;
        this.link = link + "-" + version + ".jar";
        this.version = version;
    }

    public boolean install() {
        PluginManager pm = Bukkit.getPluginManager();

        // Plugin is already installed, no need for us to install it again.
        if (pm.getPlugin(plugin) != null) {
            return false;
        }

        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            InputStream in = connection.getInputStream();

            // File path should be server/plugins/PluginName-1.0.0.jar
            String name = "plugins" + File.separator + plugin;
            if (version != null)
                name += "-" + version;
            name += ".jar";

            File target = new File(name);
            Files.copy(in, target.toPath());
            Plugin plugin = pm.loadPlugin(target);
            plugin.onLoad();
            return true;

        } catch (IOException | InvalidPluginException | InvalidDescriptionException ex) {
            throw new InternalError(ex);
        }
    }
}
