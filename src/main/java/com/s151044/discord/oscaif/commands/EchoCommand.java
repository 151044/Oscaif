package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


import java.util.List;

public class EchoCommand implements Command {
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        Messages.sendMessage(evt, arguments);
    }

    @Override
    public List<String> alias() {
        return List.of("print");
    }

    @Override
    public String callName() {
        return "echo";
    }

    @Override
    public String shortHelp() {
        return "Prints the arguments verbatim.";
    }

    @Override
    public String longHelp() {
        return "Prints the arguments verbatim. What else is there to say?";
    }
}
