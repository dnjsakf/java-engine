package com.dochi.labs.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jetty.SlackAppServer;


public class SlackApp {

    public static void main(String[] args) throws Exception {
        final String SLACK_BOT_TOKEN = System.getenv("SLACK_BOT_TOKEN");
        final String SLACK_SIGNING_SECRET = System.getenv("SLACK_SIGNING_SECRET");
        
        System.out.println(String.format("SLACK_BOT_TOKEN: %s", SLACK_BOT_TOKEN));
        System.out.println(String.format("SLACK_SIGNING_SECRET: %s", SLACK_SIGNING_SECRET));

        App app = new App();

        app.command("/hello", (req, ctx) -> {
            return ctx.ack(":wave: Hello!");
        });
        
        app.command("/echo", (req, ctx) -> {
            return ctx.ack("hi");
        });

        // http://localhost:3000/slack/events
        SlackAppServer server = new SlackAppServer(app);
        server.start(); 
    }
}
