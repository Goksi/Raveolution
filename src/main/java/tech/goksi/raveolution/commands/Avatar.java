package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collections;

public class Avatar extends SlashCommand {
    public Avatar(){
        this.name = "avatar";
        this.cooldown = 30;
        this.help = "Shows your or selected member avatar";
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "Select user which avatar you want to show"));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User u = event.optUser("user", event.getUser());
        String url = u.getAvatarUrl() + "?size=2048";
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(u.getAsTag() + " avatar");
        eb.setTitle("Avatar URL link", url);
        eb.setColor(Color.RED);
        eb.setImage(url);
        event.replyEmbeds(eb.build()).queue();
    }
}
