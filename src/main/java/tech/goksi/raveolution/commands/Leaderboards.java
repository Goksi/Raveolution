package tech.goksi.raveolution.commands;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import tech.goksi.raveolution.Bot;

import java.awt.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Leaderboards extends SlashCommand {
    public Leaderboards(){
        this.name = "leaderboards";
        this.defaultEnabled = true;
        this.cooldown = 20;
        this.help = "Shows current ranking based on level";

    }
    @Override
    protected void execute(SlashCommandEvent event) {
        LinkedHashMap<String, String> leaderboard = Bot.getInstance().getDatabase().getLevelLeaderboards();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for(Map.Entry<String, String> e : leaderboard.entrySet()){
            sb.append(i).append(". ").append(e.getKey()).append(" - ").append("Level: ").append(e.getValue()).append("\n");
            i++;
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Requested by " + Objects.requireNonNull(event.getMember()).getUser().getAsTag());
        eb.addField("Leaderboards", sb.toString(), false);
        eb.setTitle(Bot.getInstance().getConf().getValues().getString("Embeds.leaderboards.title"));
        eb.setThumbnail(Bot.getInstance().getConf().getValues().getString("Embeds.leaderboards.thumbnailUrl"));
        eb.setColor(Color.RED);
        event.replyEmbeds(eb.build()).queue();

    }
}