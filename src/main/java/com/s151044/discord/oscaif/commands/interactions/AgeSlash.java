package com.s151044.discord.oscaif.commands.interactions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Random;

public class AgeSlash implements SlashCommand{
    private List<String> responses = List.of("I'm not a child!", "Please direct your inquiries to /dev/null.",
    "Did you know that for every 60 minutes, an hour passes in Discord?");
    private Random rand = new Random();
    @Override
    public void action(SlashCommandInteractionEvent evt) {
        evt.reply(responses.get(rand.nextInt(responses.size()))).queue();
    }

    @Override
    public String callName() {
        return "age";
    }

    @Override
    public SlashCommandData commandInfo() {
        return Commands.slash("age", "Age.");
    }
}
