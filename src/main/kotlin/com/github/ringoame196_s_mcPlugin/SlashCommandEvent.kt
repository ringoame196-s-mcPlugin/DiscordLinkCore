package com.github.ringoame196_s_mcPlugin

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SlashCommandEvent : ListenerAdapter() {
    override fun onSlashCommand(e: SlashCommandEvent) {
        val command = e.name

        when (command) {
            DiscordCommandConst.LINK_COMMAND -> linkCommand(e)
        }
    }

    private fun linkCommand(e: SlashCommandEvent) {}
}
