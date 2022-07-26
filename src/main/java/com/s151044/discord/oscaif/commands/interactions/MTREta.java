package com.s151044.discord.oscaif.commands.interactions;

import com.google.gson.*;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MTREta implements SlashCommand {
    private static final String BASE_URL = "https://rt.data.gov.hk/v1/transport/mtr/getSchedule.php?";
    private static final HttpClient httpCli = HttpClient.newBuilder().build();
    private static final Gson gson = new GsonBuilder().create();
    private MTRData data;
    private List<String> filters = List.of("line", "destination");
    private SlashCommandInteractionEvent evt;
    private LimitedExecutor executor = new LimitedExecutor(20, TimeUnit.SECONDS, () -> {
        evt.deferReply().queue();
        InteractionHook hook = evt.getHook();
        String target = evt.getOption("station").getAsString();
        Optional<String> optAbbr = data.getStationAbbr(target);
        if(optAbbr.isEmpty()){
            hook.sendMessage("The station " + target + " has no abbreviation or does not exist.").queue();
            return;
        }

        String stationAbbr = optAbbr.get();
        Set<String> lines = data.getStationLines(stationAbbr);
        StringBuilder platforms = new StringBuilder();
        StringBuilder destinations = new StringBuilder();
        StringBuilder eta = new StringBuilder();
        String title = "Trains at " + target;

        OptionMapping filterOpt = evt.getOption("filter-by");
        OptionMapping criteriaOpt = evt.getOption("criteria");
        boolean hasFilter = false;
        int trainsFound = 0;

        if(filterOpt != null) {
            if(criteriaOpt == null) {
                hook.sendMessage("Filters must be accompanied by a criteria!").queue();
                return;
            }
            if(filterOpt.getAsString().equals("line")){
                title += ", filtered by line " + criteriaOpt.getAsString();
            } else if(filterOpt.getAsString().equals("destination")){
                title += ", filtered by destination " + criteriaOpt.getAsString();
            }
            hasFilter = true;
        }

        for(String s : lines) {
            if(hasFilter && evt.getOption("filter-by").getAsString().equals("line")){
                if(!data.getLineAbbr(evt.getOption("criteria").getAsString()).get().equals(s)) {
                    continue;
                }
            }
            try {
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "line=" + s + "&sta=" + stationAbbr)).build();
                String result = httpCli.send(req, HttpResponse.BodyHandlers.ofString()).body();

                JsonObject root = gson.fromJson(result, JsonElement.class).getAsJsonObject();
                int status = root.get("status").getAsInt();
                if(status != 1) {
                    hook.sendMessageEmbeds(EmbedHelper.getEmbed("**MTR has returned an error.**\nMessage: "
                            + root.get("message"))).queue();
                    return;
                }

                JsonObject jsonData = root.getAsJsonObject("data").getAsJsonObject(s + "-" + stationAbbr);
                JsonArray up = jsonData.getAsJsonArray("UP");
                if(up != null) {
                    trainsFound += parseTrainTimes(platforms, destinations, eta, up, hasFilter);
                }
                JsonArray down = jsonData.getAsJsonArray("DOWN");
                if(down != null) {
                    trainsFound += parseTrainTimes(platforms, destinations, eta, down, hasFilter);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(trainsFound == 0){
            hook.sendMessageEmbeds(EmbedHelper.getEmbed("Unable to find any trains matching that criteria!", title + ":")).queue();
        } else {
            EmbedBuilder build = new EmbedBuilder();
            build.addField("Platform", platforms.toString(), true)
                    .addField("Destination", destinations.toString(), true)
                    .addField("ETA", eta.toString(), true)
                    .setColor(Color.CYAN)
                    .setTitle(title + ":");
            hook.sendMessageEmbeds(build.build()).queue();
        }
    });

    private int parseTrainTimes(StringBuilder platforms, StringBuilder destinations, StringBuilder eta, JsonArray up, boolean hasFilter) {
        int trainsFound = 0;
        InteractionHook hook = evt.getHook();
        for(JsonElement entries : up){
            JsonObject train = entries.getAsJsonObject();
            Optional<String> optName = data.fullFromAbbr(train.get("dest").getAsString());
            String fullName = train.get("dest").getAsString();
            if(hasFilter && evt.getOption("filter-by").getAsString().equals("destination")){
                if(!data.getStationAbbr(evt.getOption("criteria").getAsString()).get().equals(fullName)) {
                    continue;
                }
            }
            if(optName.isEmpty()){
                hook.sendMessage("Warning: Unable to resolve station name for "
                        + train.get("dest").getAsString() + ".").queue();
            } else {
                fullName = optName.get();
            }
            destinations.append(fullName).append("\n");
            platforms.append(train.get("plat").getAsInt()).append("\n");
            LocalDateTime etaTime = LocalDateTime.parse(train.get("time").getAsString().replace(' ', 'T'));
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC+8"));
            eta.append(Duration.between(now, etaTime).toMinutes()).append(" min(s)\n");
            trainsFound++;
        }
        return trainsFound;
    }

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
