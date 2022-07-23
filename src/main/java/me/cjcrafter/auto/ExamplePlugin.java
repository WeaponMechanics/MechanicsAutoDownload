package me.cjcrafter.auto;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * This is an example plugin that will automatically download MechanicsCore
 * and WeaponMechanics {@link #onLoad()}. Note that often, in your plugin, you
 * will be using classes from MechanicsCore in your main class. In this event,
 * you should be using the "Loader Structure", to avoid class definition
 * exceptions. You can see an example of this
 * <a href="https://github.com/WeaponMechanics/MechanicsMain/blob/master/WeaponMechanics/src/main/java/me/deecaad/weaponmechanics/WeaponMechanicsLoader.java">in WeaponMechanics</a>.
 */
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        getLogger().log(Level.INFO, "Downloading MechanicsCore and WeaponMechanics");

        AutoMechanicsDownload download = new AutoMechanicsDownload(10000, 30000);
        download.MECHANICS_CORE.install();
        download.WEAPON_MECHANICS.install();
    }
}