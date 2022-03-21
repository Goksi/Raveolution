
package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.LevelBar;

import java.awt.*;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class Level extends SlashCommand {
    public Level(){
        this.name = "level";
        this.cooldown = 15;
        this.help = "Display your or other user leveling stats";
        this.defaultEnabled = true;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "Show users leveling stats"));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User u = event.optUser("user", event.getUser());
        LevelBar lb = new LevelBar(Bot.getInstance().getDatabase().getXP(u));
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle(Objects.equals(event.getUser(), u) ? "Your stats" : u.getName()+ "'s stats");
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Requested by " + event.getUser().getAsTag());
        eb.addField("Level", "Current level: " + lb.getCurrentLevel() + " | " + lb + " | " + "Next level: " + lb.getNextLevel(), false);
        event.replyEmbeds(eb.build()).queue();
    }
}