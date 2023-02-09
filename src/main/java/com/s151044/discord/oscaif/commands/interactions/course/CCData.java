package com.s151044.discord.oscaif.commands.interactions.course;

import com.s151044.discord.oscaif.commands.interactions.SlashCommand;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.stream.Collectors;

public class CCData implements SlashCommand {
    private static final List<String> OLD_CC = List.of("S&T", "SA", "H", "Arts", "QR", "E-Comm", "C-Comm");
    private static final List<String> NEW_CC = List.of("S", "T", "SA", "A", "H", "CTDL", "HMW");
    private CourseData data;
    public CCData(CourseData data) {
        this.data = data;
    }
    @Override
    public void action(SlashCommandInteractionEvent evt) {
        evt.deferReply().queue();
        InteractionHook hook = evt.getHook();
        int ccCredits = evt.getOption("credits").getAsInt();
        String area = evt.getOption("area").getAsString();
        boolean sscOnly = false;
        OptionMapping ssc = evt.getOption("ssc");
        if (ssc != null) {
            sscOnly = ssc.getAsBoolean();
        }
        List<Course> courses = data.getByCcArea(area, ccCredits, sscOnly);
        StringBuilder embedString = new StringBuilder();
        String title = ccCredits + "-credit " + area + " CCs ";
        if (sscOnly) {
            title += "(SSC) ";
        }
        embedString.append("\n");
        courses.forEach(course -> embedString.append(String.format("**%s %s** - %s (%d credit(s))",
                course.getDept(),
                course.getCode(),
                course.getTitle(),
                course.getCredits())).append("\n"));
        EmbedHelper.getLongEmbed(embedString.toString(), title)
                .forEach(embed -> hook.sendMessageEmbeds(embed).queue());
    }

    @Override
    public String callName() {
        return "cc-area";
    }

    @Override
    public SlashCommandData commandInfo() {
        return Commands.slash("cc-area", "Lists courses in that common core area.")
                .addOption(OptionType.INTEGER, "credits", "The number of required credits for the CC program.",
                        true, true)
                .addOption(OptionType.STRING, "area", "The CC area to query.", true, true)
                .addOption(OptionType.BOOLEAN, "ssc", "Filters by SSCs only. Works for 36-CC.");
    }

    @Override
    public void handleAutocomplete(CommandAutoCompleteInteractionEvent evt) {
        String option = evt.getFocusedOption().getName();
        String prefix = evt.getFocusedOption().getValue();
        switch (option) {
            case "credits" -> evt.replyChoiceLongs(30, 36).queue();
            case "area" -> {
                // Dynamically doing this is too annoying
                OptionMapping optionData = evt.getOption("credits");
                if(optionData == null) {
                    evt.replyChoiceStrings(List.of()).queue();
                    return;
                }
                int data = optionData.getAsInt();
                if (data == 30) {
                    evt.replyChoiceStrings(NEW_CC.stream()
                            .filter(s -> s.startsWith(prefix))
                            .collect(Collectors.toList()))
                            .queue();
                } else if (data == 36) {
                    evt.replyChoiceStrings(OLD_CC.stream()
                            .filter(s -> s.startsWith(prefix))
                            .collect(Collectors.toList()))
                            .queue();
                } else {
                    evt.replyChoiceStrings(List.of()).queue();
                }
            }
        }
    }
}
