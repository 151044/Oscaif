package com.s151044.discord.oscaif.commands.interactions;

import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MTREta implements SlashCommand {
    private MTRData data;
    private List<String> filters = List.of("line", "destination");
    private SlashCommandInteractionEvent evt;
    private LimitedExecutor executor = new LimitedExecutor(20, TimeUnit.SECONDS, () -> {
        evt.deferReply().queue();
        InteractionHook hook = evt.getHook();

    });

    public MTREta(MTRData data) {
        this.data = data;
    }

    @Override
    public void action(SlashCommandInteractionEvent evt) {
        long delayMs = executor.getDelay(TimeUnit.MILLISECONDS);
        if(delayMs > 0) {
            evt.reply("The query action is on delay! Please wait for " + Messages.toTime(delayMs)
                    + " before trying this again.").queue();
        } else {
            this.evt = evt;
            executor.queueExecution();
        }
    }

    @Override
    public String callName() {
        return "mtr-eta";
    }

    @Override
    public SlashCommandData commandInfo() {
        return Commands.slash("mtr-eta", "Checks MTR arrivals at particular stations.")
                .addOption(OptionType.STRING, "station", "The MTR station to query.", true, true)
                .addOption(OptionType.STRING, "filter-by", "Filter search with specific criteria.", false, true)
                .addOption(OptionType.STRING, "criteria", "Term to search for with respect to filter-by.", false, true);
    }

    @Override
    public void handleAutocomplete(CommandAutoCompleteInteractionEvent evt) {
        String option = evt.getFocusedOption().getName();
        String prefix = evt.getFocusedOption().getValue();
        if(option.equals("station")){
            evt.replyChoiceStrings(data.getStationSuggestions(prefix)).queue();
        } else if(option.equals("filter-by")){
            evt.replyChoiceStrings(filters.stream()
                    .filter(str -> str.startsWith(prefix))
                    .collect(Collectors.toList())).queue();
        } else if(option.equals("criteria")) {
            OptionMapping optionData = evt.getOption("filter-by");
            if(optionData == null) {
                evt.replyChoiceStrings(List.of()).queue();
                return;
            }
            String value = optionData.getAsString();
            if(value.equals("line")) {
                evt.replyChoiceStrings(data.getLineSuggestions(prefix)).queue();
            } else if(value.equals("destination")){
                evt.replyChoiceStrings(data.getStationSuggestions(prefix)).queue();
            }
        }
    }
}
