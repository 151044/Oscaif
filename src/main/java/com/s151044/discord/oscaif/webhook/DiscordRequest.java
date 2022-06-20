package com.s151044.discord.oscaif.webhook;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A representation of a webhook request to Discord.
 */
public class DiscordRequest {
    private URI uri;
    private String content;
    private HttpClient client = HttpClient.newHttpClient();
    private HttpRequest request;

    /**
     * Creates a new DiscordRequest with the specified URI and the content to be sent.
     * @param uri The URI of the webhook
     * @param content The content of the mssage to send
     */
    public DiscordRequest(URI uri, String content){
        this.uri = uri;
        this.content = content;
        request = makeRequest(uri,content);
    }

    /**
     * Sends the prepared request to Discord.
     * @return The HTTP response returned by Discord
     * @throws IOException If there is a network error when sending the request
     * @throws InterruptedException If the sending process is interrupted... somehow?
     */
    public HttpResponse<String> send() throws IOException, InterruptedException {
        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
        return res;
    }

    /**
     * Sets the content to send with this request.
     * @param content The content to send
     */
    public void setContent(String content) {
        this.content = content;
        request = makeRequest(uri,content);
    }

    private static HttpRequest makeRequest(URI uri,String content){
        return HttpRequest.newBuilder(uri).setHeader("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString("{\n\t\"content\":\"" + content + "\"\n}")).build();
    }

    /**
     * Gets the content of the message to be sent.
     * @return The message to send
     */
    public String getContent(){
        return content;
    }
}

