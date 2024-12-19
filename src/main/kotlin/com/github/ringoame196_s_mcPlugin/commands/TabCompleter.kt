package com.github.ringoame196_s_mcPlugin.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {
    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf(McCommandConst.LINK_COMMAND, McCommandConst.UN_LINK_COMMAND, McCommandConst.SHOW_COMMAND)
            2 -> when (args[0]) {
                McCommandConst.SHOW_COMMAND -> (Bukkit.getOnlinePlayers().map { it.name } + "@a" + "@p" + "@r" + "@s").toMutableList()
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }
}
