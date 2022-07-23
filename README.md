## MechanicsAutoDownload
MechanicsAutoDownload is a small library that you can shade into your plugin to automatically download and load
[MechanicsCore], [WeaponMechanics], and [ArmorMechanics]. 

## Usage

In your `build.gradle.kts`, include
```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsAutoDownload")
        credentials {
            username = "CJCrafter"
            password = "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" // this is a public token created in CJCrafter's name which will never expire
        }
    }
}
dependencies {
    compileOnly("me.cjcrafter:mechanicsautodownload:1.0.1")
}
```

Then simply copy and paste the following code into your `onLoad()` method in your main class. 
```java
        // Good idea to verbose before an IO operation, since this *could* take
        // a very long time, or even freeze your server (Depending on connection).
        getLogger().log(Level.INFO, "Downloading MechanicsCore and WeaponMechanics and ArmorMechanics");

        // Honestly I have no idea if these timeouts are important. I've set
        // them to 10 and 30 seconds without issue.
        AutoMechanicsDownload download = new AutoMechanicsDownload(10000, 30000);
        download.MECHANICS_CORE.install();
        download.WEAPON_MECHANICS.install();
        download.ARMOR_MECHANICS.install();
```



[MechanicsCore]: https://www.spigotmc.org/resources/weaponmechanics-1-9-4-1-19.99913/updates
[WeaponMechanics]: https://www.spigotmc.org/resources/weaponmechanics-1-9-4-1-19.99913/updates
[ArmorMechanics]: https://www.spigotmc.org/resources/armormechanics.103179/
