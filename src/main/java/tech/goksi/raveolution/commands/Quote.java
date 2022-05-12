package tech.goksi.raveolution.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.utils.JsonUtils;
import tech.goksi.raveolution.utils.LevelUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Quote extends SlashCommand {
    private final Logger logger;
    public Quote(){
        this.name = "quote";
        this.cooldown = 30;
        this.help = "Returns random quote of given anime or anime character";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "type", "Select quote type").addChoice("Anime", "Anime").
                addChoice("Character", "Character").setRequired(false));
        options.add(new OptionData(OptionType.STRING, "name", "Name of anime or character").setRequired(false));
        this.options = options;
        logger = LoggerFactory.getLogger(Quote.class.getName());
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String type = event.optString("type");
        String name = event.optString("name");
        String url, anime,character,quote;
        boolean array = false;
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        if(type == null){
            url = "https://animechan.vercel.app/api/random";
        }else if (type.equals("Anime")){
            if(name == null){
                eb.setDescription(":no_entry: Please specify anime name!");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();

                return;
            }
            url = "https://animechan.vercel.app/api/quotes/anime?title=%name%&page=%random%".replaceAll("%name%", name).
                    replaceAll("%random%", String.valueOf(LevelUtils.randomXp(0,10)));
            array = true;
        }else {
            if(name == null){
                eb.setDescription(":no_entry: Please specify character name!");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                return;
            }
            url = "https://animechan.vercel.app/api/quotes/character?name=%name%&page=%random%".replaceAll("%name%", name).
                    replaceAll("%random%", String.valueOf(LevelUtils.randomXp(0,10)));
            array = true;
        }
        JsonUtils ju = new JsonUtils();
        String jsonString = "";
        try {
            jsonString = ju.getJsonString(url);
        }catch (FileNotFoundException ex){
            eb.setDescription(":no_entry: Wrong character/anime name");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }catch (Exception e) {
            logger.error("Error while reading json string");
            e.printStackTrace();
            return;
        }
        JsonElement element = JsonParser.parseString(jsonString);
        JsonObject json;
        if(array){
            json = element.getAsJsonArray().get(LevelUtils.randomXp(0,5)).getAsJsonObject();
        }else {
            json = element.getAsJsonObject();
        }
        anime = json.get("anime").getAsString();
        character = json.get("character").getAsString();
        quote = json.get("quote").getAsString();
        eb.setDescription(quote);
        eb.setFooter(character + " @ " + anime);
        event.replyEmbeds(eb.build()).queue();


    }
}
