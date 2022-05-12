package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Channel;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.Objects;

public class SetupPrivate extends SlashCommand {
    public SetupPrivate(){
        this.name = "setupprivate";
        this.help = "Creating category and channel for private voice channels";
        this.cooldown = 30;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        /*should add check if channel and cat already exist*/
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
