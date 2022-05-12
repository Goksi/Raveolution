package tech.goksi.raveolution.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.utils.JsonUtils;

import java.awt.*;
import java.util.Collections;

public class CoronaApi extends SlashCommand {
    public CoronaApi(){
        this.name = "corona";
        this.help = "Get corona stats for given country";
        this.cooldown = 30;
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "country", "Select country for stats").setRequired(true));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String country = event.optString("country");
        String url = "https://coronavirus-19-api.herokuapp.com/countries/" + country;
        JsonUtils ju = new JsonUtils();
        String jsonString;
        try{
            jsonString = ju.getJsonString(url);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        EmbedBuilder eb = new EmbedBuilder();
        if(jsonString.equals("Country not found")){
            eb.setDescription(":no_entry: Sorry, but provided country couldn't be found");
            eb.setColor(Color.RED);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        JsonElement element = JsonParser.parseString(jsonString);
        JsonObject json = element.getAsJsonObject();
        long cases = json.get("cases").getAsLong();
        long todayCases = json.get("todayCases").getAsLong();
        long deaths = json.get("deaths").getAsLong();
        long active = json.get("active").getAsLong();
        long critical = json.get("critical").getAsLong();
        assert country != null;
        country = country.substring(0, 1).toUpperCase() + country.substring(1);
        eb.setTitle("Corona stats for " + country);
        eb.setThumbnail("https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/SARS-CoV-2_without_background.png/1020px-SARS-CoV-2_without_background.png");
        eb.addField("Cases: ", String.valueOf(cases), true);
        eb.addField("Critical cases: ", String.valueOf(critical), true);
        eb.addField("Active cases: " , String.valueOf(active), true);
        eb.addField("Today cases: ", String.valueOf(todayCases), false);
        eb.addField("Total deaths: ", String.valueOf(deaths), false);
        eb.setColor(Color.red);
        event.replyEmbeds(eb.build()).queue();

    }
}
