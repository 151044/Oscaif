package com.s151044.discord.oscaif.commands.slash;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message m = event.getMessage();
        if(m.getContentRaw().equals("Beautiful Colour Boomer")){
            Messages.sendMessage(m, "Indeed");
        }
    }
}
