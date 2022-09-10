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

    public MechanicsDownloader(String plugin, String link, String version) {
        this.plugin = plugin;
        this.link = link + "-" + version + ".jar";
        this.version = version;
    }

    public boolean install() {
        PluginManager pm = Bukkit.getPluginManager();

        // Plugin is already installed, no need for us to install it again.
        if (pm.getPlugin(plugin) != null) {
            System.out.println("WM - No need to install since it " + plugin + " is already there");
            return false;
        }

        try {
            System.out.println("WM - Attempting install");
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            InputStream in = connection.getInputStream();

            File target = new File("plugins" + File.separator + plugin + "-" + version + ".jar");
            Files.copy(in, target.toPath());
            Plugin plugin = pm.loadPlugin(target);
            plugin.onLoad();
            return true;

        } catch (IOException | InvalidPluginException | InvalidDescriptionException ex) {
            throw new InternalError(ex);
        }
    }
}
