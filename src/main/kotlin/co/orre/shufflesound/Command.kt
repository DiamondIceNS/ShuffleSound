package co.orre.shufflesound

import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.*


class Command(private val plugin: Plugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        var volume = 30000000f
        var pitch = 1f

        if (args.isEmpty() || args.size > 3) return false
        if (args.size >= 2) {
            val v = args[1].toFloatOrNull()
            if (v != null) {
                volume = when {
                    v < 0f -> 0f
                    v > 30000000f -> 30000000f
                    else -> v
                }
            }
        }

        if (args.size == 3) {
            val p = args[2].toFloatOrNull()
            if (p != null)
                pitch = when {
                    p < 0.5f -> 0.5f
                    p > 2f -> 2f
                    else -> p
                }
        }

        val sounds = plugin.soundsConfig.data.getStringList("sounds")
        val sound = sounds[Random().nextInt(sounds.count())]

        when (args[0]) {
            "@p" -> {
                val o = when (sender) {
                    is BlockCommandSender -> sender.block.location
                    is Player -> sender.location
                    else -> null
                }
                if (o == null) { sender.sendMessage("This command sender cannot use @p with this command"); return true }
                var closestPlayer: Player? = null
                var shortestDistance = -1.0
                for (p in Bukkit.getOnlinePlayers()) {
                    if (p.world != o.world) continue
                    val l = p.location
                    val dist = Math.sqrt(Math.pow(o.x - l.x, 2.0) + Math.pow(o.y - l.y, 2.0) + Math.pow(o.z - l.z, 2.0))
                    if (dist < shortestDistance || shortestDistance == -1.0) {
                        closestPlayer = p
                        shortestDistance = dist
                    }
                }
                if (closestPlayer == null) { sender.sendMessage("Could not find any players"); return true }
                closestPlayer.playSound(closestPlayer.location, sound, volume, pitch)
                return true
            }
            "@e" -> {
                sender.sendMessage("@e is not supported for this command"); return true
            }
            "@a" -> {
                for (p in Bukkit.getOnlinePlayers())
                    p.playSound(p.location, sound, volume, pitch)
                return true
            }
            "@s" -> {
                when (sender) {
                    is Player -> sender.playSound(sender.location, sound, volume, pitch)
                    else -> sender.sendMessage("This command sender cannot use @s with this command")
                }
                return true
            }
            "@r" -> {
                val players = Bukkit.getOnlinePlayers().toList()
                if (players.isEmpty()) { sender.sendMessage("No one is online"); return true}
                val p = players[Random().nextInt(players.size)]
                p.playSound(p.location, sound, volume, pitch)
                return true
            }
            else -> {
                val target = Bukkit.getPlayer(args[0])
                if (target == null) {
                    sender.sendMessage("Could not find player: ${args[0]}")
                    return true
                }
                target.playSound(target.location, sound, volume, pitch)
                return true
            }
        }
    }
}