package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;

public class Private extends SlashCommand {
    public Private(){
        this.name = "private";
        this.help = "Options for your private channel";
        this.defaultEnabled = true;
        this.children = new SlashCommand[]{new Add(this), new Remove(this)};
    }
    @Override
    protected void execute(SlashCommandEvent event) {
      /*Nothing to see here :(*/

    }
    public void send(SlashCommandEvent e){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription(ConfigUtils.getString("SetupPrivate.channelId").equals("Put you channel id here") || ConfigUtils.getString("SetupPrivate.channelId") == null ?
                ":no_entry: Please notify server owner to setup private channels in config.yml" :
                ":no_entry: You currently dont own private channel, join " +
                        Objects.requireNonNull(e.getJDA().getVoiceChannelById(ConfigUtils.getString("SetupPrivate.channelId"))).getAsMention() + " to create one");
        e.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
}

class Add extends SlashCommand{
    private final Private priv;
    public Add(Private priv){
        this.name = "add";
        this.help = "Adds user to your private channel";
        this.defaultEnabled = true;
        this.cooldown = 10;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "User you want to add to your private channel").setRequired(true));
        this.priv = priv;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User target = event.optUser("user");
        assert target != null;
        Member targetM = Objects.requireNonNull(event.getGuild()).retrieveMember(target).complete();
        if(Bot.getInstance().getPrivateChannels().containsKey(event.getUser().getIdLong())){
            VoiceChannel priv = event.getJDA().getVoiceChannelById(Bot.getInstance().getPrivateChannels().get(event.getUser().getIdLong()));
            assert priv != null;
            if(priv.getPermissionOverride(targetM) == null){
                priv.createPermissionOverride(targetM).setAllow(
                        Permission.VIEW_CHANNEL,
                        Permission.VOICE_CONNECT
                ).reason("Adding user to the channel").queue();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.RED);
                eb.setDescription(":white_check_mark: You successfully added " + target.getAsTag() + " to your private channel");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.RED);
                eb.setDescription(":no_entry: That user is already invited to your private");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }else {
            priv.send(event);
        }

    }
}
class Remove extends SlashCommand{
    private final Private priv;
    public Remove(Private priv){
        this.name = "remove";
        this.help = "Removes user from your private channel";
        this.defaultEnabled = true;
        this.cooldown = 10;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "User you want to remove from private channel").setRequired(true));
        this.priv = priv;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User target = event.optUser("user");
        assert target != null;
        Member targetM = Objects.requireNonNull(event.getGuild()).retrieveMember(target).complete();
        if(Bot.getInstance().getPrivateChannels().containsKey(event.getUser().getIdLong())){
            VoiceChannel priv = event.getJDA().getVoiceChannelById(Bot.getInstance().getPrivateChannels().get(event.getUser().getIdLong()));
            assert priv != null;
            if(priv.getPermissionOverride(targetM) != null){
                Objects.requireNonNull(priv.getPermissionOverride(targetM)).delete().queue();
                if(Objects.equals((Objects.requireNonNull(targetM.getVoiceState())).getChannel(), priv)){
                    event.getGuild().kickVoiceMember(targetM).queue();
                }
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.RED);
                eb.setDescription(":white_check_mark: You successfully removed " + target.getAsTag() + " from your private channel");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.red);
                eb.setDescription(":no_entry: User " + target.getAsTag() + " is not even invited to your private channel");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }

        }else {
            priv.send(event);
        }

    }


}