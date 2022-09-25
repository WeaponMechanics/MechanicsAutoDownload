package me.cjcrafter.auto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AutoMechanicsDownload {

    public final MechanicsDownloader MECHANICS_CORE;
    public final MechanicsDownloader WEAPON_MECHANICS;
    public final MechanicsDownloader ARMOR_MECHANICS;

    public final String RESOURCE_PACK_VERSION;

    public AutoMechanicsDownload(FileConfiguration config) {
        this(config.getInt("Auto_Download.Connection_Timeout", 10) * 1000, config.getInt("Auto_Download.Read_Timeout", 30) * 1000);
    }

    public AutoMechanicsDownload(int connectionTimeout, int readTimeout) {
        String coreVersion = null;
        String weaponVersion = null;
        String armorVersion = null;
        String resourcePackVersion = null;

        // IO operations
        try {
            String link = "https://api.github.com/repos/WeaponMechanics/MechanicsMain/releases/latest";
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray assets = json.getAsJsonArray("assets");

            for (JsonElement asset : assets) {
                String fileName = asset.getAsJsonObject().get("name").getAsString();

                // Works in cases like
                // "MechanicsCore-1.4.10.jar
                // "WeaponMechanicsResourcePack-1.4.10.zip
                // "MechanicsCore-1.4.10-BETA.jar
                // "WeaponMechanicsResourcePack-1.4.10-BETA.zip
                String[] split = fileName.split("-");

                // E.g. when "WeaponMechanics.zip"
                if (split.length == 1) continue;

                String id = split[0];
                String version = split[1];
                if (split.length < 3) {
                    // "1.4.10.jar"
                    // "1.4.10.zip"
                    // -> remove the jar/zip
                    // With -BETA.jar/zip this wont happen because its already "1.4.10"
                    version = version.substring(0, version.length() - 4);
                }

                switch (id) {
                    case "MechanicsCore":
                        coreVersion = version;
                        break;
                    case "WeaponMechanics":
                        weaponVersion = version;
                        break;
                    case "WeaponMechanicsResourcePack":
                        resourcePackVersion = version;
                        break;
                }
            }
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        try {
            String link = "https://api.github.com/repos/WeaponMechanics/ArmorMechanics/releases/latest";
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            armorVersion = json.get("tag_name").getAsString();

        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        MECHANICS_CORE = new MechanicsDownloader("MechanicsCore", "https://github.com/WeaponMechanics/MechanicsMain/releases/latest/download/MechanicsCore", coreVersion);
        WEAPON_MECHANICS = new MechanicsDownloader("WeaponMechanics", "https://github.com/WeaponMechanics/MechanicsMain/releases/latest/download/WeaponMechanics", weaponVersion);
        ARMOR_MECHANICS = new MechanicsDownloader("ArmorMechanics", "https://github.com/WeaponMechanics/ArmorMechanics/releases/latest/download/ArmorMechanics", armorVersion);
        RESOURCE_PACK_VERSION = resourcePackVersion;
    }
}
