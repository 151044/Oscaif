package com.s151044.discord.oscaif.commands.map;

import com.s151044.discord.oscaif.commands.Command;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class ListMap implements Command {

    private Map<String, String> toUrl;

    public ListMap(Map<String, String> toUrl){
        this.toUrl = toUrl;
    }
    @Override
    public void action(GuildMessageReceivedEvent evt, String callName, String arguments) {
        StringBuilder sb = new StringBuilder();
        toUrl.forEach((key, value) -> sb.append(key).append("\n"));
        Messages.sendMessage(evt, EmbedHelper.getEmbed(sb.toString(), "Available Mappings:"));
    }

    @Override
    public List<String> alias() {
        return List.of("ls");
    }

    @Override
    public String callName() {
        return "list";
    }

    @Override
    public String shortHelp() {
        return "Lists all registered mappings.";
    }

    @Override
    public String longHelp() {
        return "Lists all mapping which have been registered by &map.";
    }
}
