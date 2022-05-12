package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.exceptions.UserHaveTicketException;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.util.List;
import java.util.Objects;

public class TicketManager {
    private final Member ticketCreator;
    private final Guild g;
    public TicketManager(Member creator, Guild guild){
        this.ticketCreator = creator;
        this.g = guild;
    }

    public TextChannel create(JDA jda) throws UserHaveTicketException {
        if(Bot.getInstance().getTickets().containsKey(ticketCreator.getIdLong())) throw new UserHaveTicketException("User already have ticket open");
        Category ticketCategory = jda.getCategoryById(ConfigUtils.getString("Tickets.ticketsCategoryId"));
        assert ticketCategory != null;
        int id = Bot.getInstance().getTickets().size()+1;
        TextChannel ticketChannel = ticketCategory.createTextChannel(ConfigUtils.getString("Tickets.ticketName").replaceAll("%id%", String.valueOf(id))).complete();

        Bot.getInstance().getTickets().put(ticketCreator.getIdLong(), ticketChannel.getIdLong());
        ticketChannel.upsertPermissionOverride(ticketCreator).setAllowed(
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.VIEW_CHANNEL
        ).reason("Adding ticket creator to the channel").queue();
        ticketChannel.upsertPermissionOverride(g.getSelfMember()).setAllowed(
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_MANAGE
        ).reason("Adding bot to the channel").queue();
        List<String> supportRoles = ConfigUtils.getStringList("Tickets.supportRoles");
        for(String ids : supportRoles){
            ticketChannel.upsertPermissionOverride(Objects.requireNonNull(jda.getRoleById(ids))).setAllowed(
                    Permission.MESSAGE_SEND,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI,
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_MANAGE
            ).reason("Adding staff roles to the channel").queue();
        }
        ticketChannel.upsertPermissionOverride(g.getPublicRole()).setDenied(
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.VIEW_CHANNEL
        ).reason("Removing everyone else").queue();

        return ticketChannel;
    }
}
