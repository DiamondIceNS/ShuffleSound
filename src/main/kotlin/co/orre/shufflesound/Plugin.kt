package co.orre.shufflesound

import com.google.gson.GsonBuilder
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader

class Plugin : JavaPlugin() {

    var soundsConfig: DataConfigAccessor = DataConfigAccessor(this, dataFolder, "sounds.yml")

    override fun onEnable() {

        //ConfigurationSerialization.registerClass(SoundDir::class.java, "SoundDir")
        val sounds: MutableList<String> = mutableListOf()

        val jsonFolder = File(dataFolder, "json")
        if (!jsonFolder.exists()) jsonFolder.mkdirs()
        jsonFolder.listFiles()
                .map { gson.fromJson(FileReader(it), HashMap<String, Any>().javaClass) }
                .forEach { sounds.addAll(it.keys) }
        sounds.sort()
        soundsConfig.data.set("sounds", sounds)
        soundsConfig.saveConfig()
        soundsConfig.reloadConfig()

        getCommand("shuffle").executor = Command(this)

    }

    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()!!
    }

}