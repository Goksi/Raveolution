package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.util.Map;

public class PrivateHandler extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        create((VoiceChannel) event.getChannelJoined(), event.getMember());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        remove((VoiceChannel) event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        VoiceChannel from = (VoiceChannel) event.getChannelLeft();
        VoiceChannel to = (VoiceChannel) event.getChannelJoined();
        remove(from);
        create(to, event.getMember());
    }

    private void remove(VoiceChannel c){
        if(Bot.getInstance().getPrivateChannels().containsValue(c.getIdLong()) && c.getMembers().size() == 0){
            long key = 0;
            for(Map.Entry<Long, Long> e : Bot.getInstance().getPrivateChannels().entrySet()){
                if(e.getValue() == c.getIdLong()){
                    key = e.getKey();
                    break;
                }
            }
            c.delete().submit();
            Bot.getInstance().getPrivateChannels().remove(key);
        }
    }

    private void create(VoiceChannel c, Member m){
        if(c.getId().equals(ConfigUtils.getString("SetupPrivate.channelId"))){
            if(Bot.getInstance().getPrivateChannels().containsKey(m.getIdLong())){
                VoiceChannel priv = c.getJDA().getVoiceChannelById(Bot.getInstance().getPrivateChannels().get(m.getIdLong()));
                c.getGuild().moveVoiceMember(m, priv).queue();
                return;
            }
            Category privCat = c.getJDA().getCategoryById(ConfigUtils.getString("SetupPrivate.categoryId"));
            assert privCat != null;
            VoiceChannel privChan = privCat.createVoiceChannel(m.getEffectiveName() + "'s private").complete();
            privChan.upsertPermissionOverride(m).setAllowed(
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_CONNECT,
                    Permission.VOICE_MUTE_OTHERS,
                    Permission.VOICE_DEAF_OTHERS,
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_PERMISSIONS
            ).reason("Creating priv channel").queue();
            privChan.upsertPermissionOverride(c.getGuild().getPublicRole()).setDenied(
                    Permission.VIEW_CHANNEL
            ).reason("Removing everyone else").queue();
            c.getGuild().moveVoiceMember(m, privChan).queue();
            Bot.getInstance().getPrivateChannels().put(m.getIdLong(), privChan.getIdLong());
        }
    }
}
