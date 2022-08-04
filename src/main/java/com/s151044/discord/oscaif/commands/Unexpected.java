package com.s151044.discord.oscaif.commands;

import com.s151044.discord.oscaif.utils.Messages;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Unexpected implements Command{
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        Messages.send(evt, "https://tenor.com/view/inquisition-monty-python-spanish-inquisition-walk-in-gif-17117394");
    }

    @Override
    public List<String> alias() {
        return List.of("expect", "unexpect");
    }

    @Override
    public String callName() {
        return "unexpected";
    }

    @Override
    public String shortHelp() {
        return "Nobody ever expects this.";
    }

    @Override
    public String longHelp() {
        return "Expecting the unexpected is impossible.";
    }
}
