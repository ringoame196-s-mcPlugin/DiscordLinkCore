package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Command : CommandExecutor {
    private val linkManager = LinkManager()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        val subCommand = args[0]
        when (subCommand) {
            McCommandConst.LINK_COMMAND -> linkCommand(sender)
        }

        return true
    }

    private fun linkCommand(sender: CommandSender) {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return
        }

        val authKey = linkManager.creationAuthKey(sender)

        if (authKey != null) {
            val message = "${ChatColor.GOLD}[認証キー] $authKey"
            sender.sendMessage(message)
        } else {
            val message = "${ChatColor.RED}認証キー生成に失敗しました"
            sender.sendMessage(message)
        }
    }
}
