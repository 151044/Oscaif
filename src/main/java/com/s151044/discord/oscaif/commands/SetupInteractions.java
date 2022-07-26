package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.commands.interactions.SlashCommand;
import com.s151044.discord.oscaif.commands.interactions.SlashCommandList;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.Command.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetupInteractions implements Command{
    private static List<String> guildIds = new ArrayList<>();
    private SlashCommandList slashList;

    public SetupInteractions(SlashCommandList slashList) {
        this.slashList = slashList;
    }

    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        if(!evt.getChannelType().isGuild()){
            Messages.sendMessage(evt, "This is not a guild channel!");
            return;
        }
        if(guildIds.contains(evt.getGuild().getId())){
            Messages.sendMessage(evt, "Interactions have been set up in this server already!");
            return;
        }
        GuildMessageChannel channel = evt.getGuildChannel();
        channel.getGuild().updateCommands()
                .addCommands(Commands.context(Type.MESSAGE, "Opposite Direction"))
                .addCommands(slashList.getCommands().stream().map(SlashCommand::commandInfo).collect(Collectors.toList()))
                        .queue();
        guildIds.add(evt.getGuild().getId());
        Messages.sendMessage(evt, "Done!");
    }

    @Override
    public List<String> alias() {
        return List.of("setslash", "setupslash");
    }

    @Override
    public String callName() {
        return "SetupInteractions";
    }

    @Override
    public String shortHelp() {
        return "Sets up slash commands in this server.";
    }

    @Override
    public String longHelp() {
        return "Sets up slash commands in this server.";
    }

    @Override
    public boolean hidden() {
        return true;
    }
}
