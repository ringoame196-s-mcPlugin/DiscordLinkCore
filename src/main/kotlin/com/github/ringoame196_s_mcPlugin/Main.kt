package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val linkManager = LinkManager()

    override fun onEnable() {
        super.onEnable()
        val plugin = this
        linkManager.makeLinkDataBaseFile(plugin)
        linkManager.resetLinkKeyTable()
        val command = getCommand("discord")
        command!!.setExecutor(Command())
        command.tabCompleter = TabCompleter()
    }
}
