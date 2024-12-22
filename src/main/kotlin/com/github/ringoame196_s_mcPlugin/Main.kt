package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.data.Data
import com.github.ringoame196_s_mcPlugin.managers.DiscordManager
import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private val plugin = this

    override fun onEnable() {
        super.onEnable()

        Data.plugin = plugin

        saveDefaultConfig() // config生成
        DiscordManager().bootDiscordBOT(plugin)

        if (!File(plugin.dataFolder, "data.db").exists()) saveResource("data.db", false)
        LinkManager().resetAuthKey()

        val command = getCommand("dcore")
        command!!.setExecutor(Command())
        command.tabCompleter = TabCompleter()
    }
}
