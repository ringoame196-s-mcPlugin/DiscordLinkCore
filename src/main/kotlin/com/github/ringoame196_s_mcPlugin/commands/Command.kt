package com.github.ringoame196_s_mcPlugin.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        val subCommand = args[0]

        when (subCommand) {
            else -> sender.sendMessage("${ChatColor.RED}構文が間違っています")
        }
        return true
    }
}
