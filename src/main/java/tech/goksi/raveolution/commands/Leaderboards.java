package tech.goksi.raveolution.commands;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.*;

public class Leaderboards extends SlashCommand {
    public Leaderboards(){
        this.name = "leaderboards";
        this.defaultEnabled = true;
        this.cooldown = 20;
        this.help = "Shows current ranking based on level";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "type", "Select what kind of leaderboard you want to show").
                addChoice("Invite", "invite").addChoice("Level", "level"));

    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String kind = event.optString("type", "level");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Requested by " + Objects.requireNonNull(event.getMember()).getUser().getAsTag());
        eb.setThumbnail(ConfigUtils.getString("Embeds.leaderboards.thumbnailUrl"));
        eb.setColor(Color.RED);
        LinkedHashMap<String, String> leaderboard;
        StringBuilder sb = new StringBuilder();
        int i = 1;
        if(kind.equals("level")){
            leaderboard = Bot.getInstance().getDatabase().getLevelLeaderboards();
            for(Map.Entry<String, String> e : leaderboard.entrySet()){
                sb.append(i).append(". ").append(e.getKey()).append(" - ").append("Level: ").append(e.getValue()).append("\n");
                i++;
            }

            eb.addField("Leaderboards", sb.toString(), false);
            eb.setTitle(ConfigUtils.getString("Embeds.leaderboards.titleLevel"));
        }else{
            leaderboard = Bot.getInstance().getDatabase().getInvitesLeaderboards();
            for(Map.Entry<String, String> e : leaderboard.entrySet()){
                sb.append(i).append(". ").append(e.getKey()).append(" - ").append("Invites: ").append(e.getValue()).append("\n");
                i++;
            }
            eb.setTitle(ConfigUtils.getString("Embeds.leaderboards.titleInvites"));
            eb.addField("Leaderboards", sb.toString(), false);

        }
        event.replyEmbeds(eb.build()).queue();


    }
}