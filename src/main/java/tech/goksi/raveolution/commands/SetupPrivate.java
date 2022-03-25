package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class SetupPrivate extends SlashCommand {
    public SetupPrivate(){
        this.name = "setupprivate";
        this.help = "Creating category and channel for private voice channels";
        this.cooldown = 30;
        this.defaultEnabled = false;
        List<String> roles = ConfigUtils.getStringList("SetupPrivate.allowedRanks");
        this.enabledRoles = roles.toArray(new String[0]);
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String channelId, categoryId;
        net.dv8tion.jda.api.entities.Category cat = Objects.requireNonNull(event.getGuild()).createCategory(ConfigUtils.getString("SetupPrivate.categoryName")).complete();
        Channel channel = cat.createVoiceChannel(ConfigUtils.getString("SetupPrivate.channelName")).complete();
        channelId = channel.getId();
        categoryId = cat.getId();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.red);
        eb.setDescription("✅ Channel and category are successfully created, you are free to change their names. \nAlso please put `" + channelId+"` for channelId and `" + categoryId
                + "` for categoryId in your config.yml");
        ConfigUtils.set("SetupPrivate.categoryId", categoryId);
        ConfigUtils.set("SetupPrivate.channelId", channelId);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
}
