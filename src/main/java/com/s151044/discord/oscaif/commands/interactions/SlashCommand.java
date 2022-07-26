package com.s151044.discord.oscaif.commands.interactions;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * The interface to represent a slash command in Discord.
 */
public interface SlashCommand {
    /**
     * The function that is called on a message sent by anyone in a guild.
     *
     * @param evt       The message received event
     */
    void action(SlashCommandInteractionEvent evt);

    /**
     * Gets the name with which this command can be invoked by a message.
     * @return The call name of this command
     */
    String callName();

    SlashCommandData commandInfo();

    default void handleAutocomplete(CommandAutoCompleteInteractionEvent evt){}
}