package com.s151044.discord.oscaif.commands;

import com.google.gson.*;
import com.s151044.discord.oscaif.utils.EmbedHelper;
import com.s151044.discord.oscaif.utils.Messages;
import com.s151044.discord.oscaif.utils.ratelimit.LimitedExecutor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GoopRetriever implements Command {
    private List<String> formatted = new ArrayList<>();
    private ScheduledExecutorService refreshTimer = Executors.newScheduledThreadPool(1);
    private GoopRequest req = new GoopRequest();
    private LimitedExecutor executor = new LimitedExecutor(5, TimeUnit.MINUTES, () -> {
        formatted = req.getPosts();
    });
    public GoopRetriever(){
        refreshTimer.scheduleAtFixedRate(() -> executor.queueExecution(), 0, 30, TimeUnit.MINUTES);
    }
    @Override
    public void action(MessageReceivedEvent evt, String callName, String arguments) {
        if(arguments.equals("refresh")) {
            long delayMs = executor.getDelay(TimeUnit.MILLISECONDS);
            if (delayMs > 0) {
                Messages.send(evt, "The refreshing action is on delay. Time left: " + Messages.toTime(delayMs));
            } else {
                Messages.send(evt, "Refreshing.");
            }
            executor.queueExecution();
        } else {
            if(formatted.size() == 0){
                Messages.send(evt, "Nothing yet...");
            } else {
                EmbedHelper.getLongEmbed(String.join("\n", formatted), "Goop Posts:").forEach(emb -> Messages.send(evt, emb));
            }
        }
    }

    @Override
    public List<String> alias() {
        return List.of();
    }

    @Override
    public String callName() {
        return "goop";
    }

    @Override
    public String shortHelp() {
        return "Gets top posts from goop.";
    }

    @Override
    public String longHelp() {
        return "Gets top posts from goop.\n" +
                "Results are cached, and updated per 30 minutes.\n" +
                "Refreshing early can be done by &goop refresh, but this action is on a timer of 5 minutes.";
    }
    private static class GoopRequest {
        private HttpClient http = HttpClient.newBuilder().build();
        private HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://goop.ai/api/community/ust"))
                .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:100.0) Gecko/20100101 Firefox/100.0")
                .build();
        private Gson gson = new GsonBuilder().create();
        public List<String> getPosts(){
            List<String> posts = new ArrayList<>();
            try {
                String result = http.send(req, HttpResponse.BodyHandlers.ofString()).body();
                JsonElement elem = gson.fromJson(result, JsonElement.class);
                JsonArray data = elem.getAsJsonObject().getAsJsonObject("data").getAsJsonArray("topics");
                for (JsonElement datum : data) {
                    JsonObject post = datum.getAsJsonObject();
                    posts.add("[" + post.get("title").getAsString() + "](https://goop.ai/topic/" + post.get("shareKey").getAsString()
                            + "/" + post.get("title").getAsString().replace(' ','-') + ")");
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to connect to goop: " + e.getMessage());
            }
            return posts;
        }
    }
}
