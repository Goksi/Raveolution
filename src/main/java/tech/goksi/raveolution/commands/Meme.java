package tech.goksi.raveolution.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.utils.JsonUtils;

import java.awt.*;
import java.util.Collections;

public class Meme extends SlashCommand {
    private final Logger logger;
    public Meme(){
        this.name = "meme";
        this.defaultEnabled = true;
        this.cooldown = 25;
        this.help = "Returns back a random reddit meme";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "subreddit", "Select subreddit you want meme from").setRequired(false).
                addChoices(new Command.Choice("wholesome", "wholesomememes"), new Command.Choice("Dank Memes", "dankmemes"),
                        new Command.Choice("Me irl", "me_irl"), new Command.Choice("Memes", "memes")));
        logger = LoggerFactory.getLogger(Meme.class.getName());
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String subreddit = event.optString("subreddit");
        String url;
        if(subreddit == null){
            url = "https://meme-api.herokuapp.com/gimme";
        }else {
            url = "https://meme-api.herokuapp.com/gimme/" + subreddit;
        }
        JsonUtils ju = new JsonUtils();
        String jsonString;
        try {
            jsonString = ju.getJsonString(url);
        } catch (Exception e) {
            logger.error("Error while trying to get json string");
            e.printStackTrace();
            return;
        }
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        String meme = json.get("url").getAsString();
        String title = json.get("title").getAsString();
        String author = json.get("author").getAsString();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle(title);
        eb.setImage(meme);
        eb.setFooter("Made by " + author);
        event.replyEmbeds(eb.build()).queue();

    }
}
