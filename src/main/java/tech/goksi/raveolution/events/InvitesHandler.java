package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.raveolution.Bot;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class InvitesHandler extends ListenerAdapter {

    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        Bot.getInstance().getInvitesUsage().put(event.getCode(), event.getInvite().getUses());
    }

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event) {
        Bot.getInstance().getInvitesUsage().remove(event.getCode());
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Map<String, Integer> currentInvites = new HashMap<>();
        for(Invite i : event.getGuild().retrieveInvites().complete()){
            currentInvites.put(i.getCode(), i.getUses());
        }
        Map<String, Integer> difference = currentInvites.entrySet().stream(). //fck discord for not adding api support for getting by who is someone invited
                filter(entry -> !entry.getValue().equals(Bot.getInstance().getInvitesUsage().get(entry.getKey()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String invCode = "";
        for(Map.Entry<String, Integer> e : difference.entrySet()){
            invCode = e.getKey();
        }
        for(Invite i : event.getGuild().retrieveInvites().complete()){
            Bot.getInstance().getInvitesUsage().put(i.getCode(), i.getUses());
        }
        User inviter = Invite.resolve(event.getJDA(), invCode).complete().getInviter();
        assert inviter != null;
        Bot.getInstance().getDatabase().invitesAdd(event.getUser(), inviter);
        if(isFake(event.getUser())){
            Bot.getInstance().getDatabase().addFakeInvite(inviter);
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        User inviter = event.getJDA().retrieveUserById(Bot.getInstance().getDatabase().getInviter(event.getUser())).complete();
        assert inviter != null;
        Bot.getInstance().getDatabase().addLeave(inviter);
    }

    private boolean isFake(User u){
        final String avatar = u.getAvatarUrl();
        final OffsetDateTime created = u.getTimeCreated();
        final long days = DAYS.between(created, OffsetDateTime.now());
        return days < 2 && avatar==null;
    }
}
