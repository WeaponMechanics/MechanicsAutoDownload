package me.cjcrafter.auto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class AutoMechanicsDownload {

    public final MechanicsDownloader MECHANICS_CORE;
    public final MechanicsDownloader WEAPON_MECHANICS;
    public final MechanicsDownloader ARMOR_MECHANICS;


    public AutoMechanicsDownload(FileConfiguration config) {
        this(config.getInt("Auto_Download.Connection_Timeout", 10) * 1000, config.getInt("Auto_Download.Read_Timeout", 30) * 1000);
    }

    public AutoMechanicsDownload(int connectionTimeout, int readTimeout) {
        String coreVersion = null;
        String weaponVersion = null;
        String armorVersion = null;

        // IO operations
        try {
            String link = "https://github.com/WeaponMechanics/MechanicsMain/releases/latest/download/versions.txt";
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            InputStream in = connection.getInputStream();
            Scanner scanner = new Scanner(in);

            // Loop through the latest versions.txt file to determine the
            // latest version. ArmorMechanics doesn't have a versions.txt,
            // so it uses a different system.
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;

                String[] split = line.split(": ?");
                String id = split[0];
                String version = split[1];

                switch (id) {
                    case "MechanicsCore":
                        coreVersion = version;
                        break;
                    case "WeaponMechanics":
                        weaponVersion = version;
                        break;
                    case "WeaponMechanicsResourcePack":
                        // This information is currently intentionally unused
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
    }
}
