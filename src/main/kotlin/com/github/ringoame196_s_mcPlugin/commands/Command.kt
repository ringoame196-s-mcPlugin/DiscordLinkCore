package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import org.bukkit.ChatColor
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
            CommandConst.LINK_COMMAND -> linkCommand(sender)
            else -> sender.sendMessage("${ChatColor.RED}構文が間違っています")
        }
        return true
    }

    private fun linkCommand(sender: CommandSender) {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return
        }

        val playerUUID = sender.uniqueId.toString()
        val discordName = linkManager.acquisitionDiscordName(playerUUID)

        if (discordName == null) {
            val authKey = linkManager.issueAuthKey(sender)

            if (authKey != null) {
                val message = "${ChatColor.GOLD}認証キーが発行されました「$authKey」"
                sender.sendMessage(message)
            } else {
                val message = "${ChatColor.RED}認証キーが正常に生成されませんでした"
            }
        } else {
            val message = "${ChatColor.RED}${discordName}と連携済みです"
            sender.sendMessage(message)
        }
    }
}
