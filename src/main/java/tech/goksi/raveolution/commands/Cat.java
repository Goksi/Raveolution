package tech.goksi.raveolution.commands;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.goksi.raveolution.utils.ConfigUtils;
import tech.goksi.raveolution.utils.JsonUtils;

import java.awt.*;

public class Cat extends SlashCommand {
    public Cat(){
        this.name = "cat";
        this.help = "Sends back a cute image of random cat <3";
        this.cooldown = 30;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String url = "https://api.thecatapi.com/v1/images/search";
        String jsonString;
        JsonUtils ju = new JsonUtils();
        try{
            jsonString = ju.getJsonString(url, "x-api-key", "e431731d-bddb-415e-8a40-704bf3d8a31c");

        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        JsonElement element = JsonParser.parseString(jsonString);
        JsonArray array = element.getAsJsonArray();
        JsonObject object = array.get(0).getAsJsonObject();
        String imageUrl = object.get("url").getAsString();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setImage(imageUrl);
        eb.setTitle(ConfigUtils.getString("Embeds.cat.title"));
        eb.setDescription(ConfigUtils.getString("Embeds.cat.desc"));
        eb.setColor(Color.ORANGE);
        event.replyEmbeds(eb.build()).queue();
    }
}
