package tech.goksi.raveolution;

import tech.goksi.raveolution.utils.ConfigUtils;


import java.util.Scanner;

public class Main {
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) {
        Bot bot = new Bot();
        String token = ConfigUtils.getString("BotInfo.token");
        String ownerId = ConfigUtils.getString("BotInfo.ownerId");
        String guildId = ConfigUtils.getString("BotInfo.guildId");
        if(token.equals("Put your token here") || token == null || token.equals("")){
            token = prompt("Looks like you didnt put your token, you can set it up now: ");
            ConfigUtils.set("BotInfo.token", token);
            bot.setEditCfg(true);
        }
        if(ownerId.equals("Put your discord id here") || ownerId == null || ownerId.equals("")){
            ownerId = prompt("Looks like you didnt put your discord id, you can set it up now: ");
            ConfigUtils.set("BotInfo.ownerId", ownerId);
            bot.setEditCfg(true);
        }
        if(guildId.equals("Put your server id here") || guildId == null || guildId.equals("")){
            guildId = prompt("Looks like you didnt put your server id, you can set it up now: ");
            ConfigUtils.set("BotInfo.guildId", guildId);
            bot.setEditCfg(true);
        }
        System.out.println("[Raveolution] Everything looks fine, starting websocket..");
        bot.setGuildId(guildId);
        bot.setOwnerId(ownerId);
        bot.setToken(token);
        bot.start();


    }


    private static String prompt(String message){
        Scanner sc = new Scanner(System.in);
        System.out.println("[Raveolution] " + message);
        return sc.nextLine();
    }
}
