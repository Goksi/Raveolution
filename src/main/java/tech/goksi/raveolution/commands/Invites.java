package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.Collections;
import java.util.Date;

public class Invites extends SlashCommand {
    public Invites(){
        this.name = "invites";
        this.cooldown = 15;
        this.help = "Shows your or selected member invite stats";
        this.defaultEnabled = true;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "Show users invites stats"));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User u = event.optUser("user", event.getUser());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Requested by " + event.getUser().getAsTag());
        eb.setTitle(ConfigUtils.getString("Embeds.invites.title"));
        eb.setColor(Color.RED);
        eb.addField("Total:", String.valueOf(Bot.getInstance().getDatabase().getInvitesTotal(u)), true);
        eb.addField("Left:", String.valueOf(Bot.getInstance().getDatabase().getLeft(u)), true);
        eb.addField("Fake:", String.valueOf(Bot.getInstance().getDatabase().getFakeInvites(u)), true);
        eb.setAuthor(u.getName()+  "'s stats");
        event.replyEmbeds(eb.build()).queue();

    }
}
