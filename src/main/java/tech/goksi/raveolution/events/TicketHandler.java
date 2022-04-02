package tech.goksi.raveolution.events;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.exceptions.UserHaveTicketException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TicketHandler extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(TicketHandler.class);
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(Objects.equals(event.getButton().getId(), "ticket")){
            Member u = event.getMember();
            assert u != null;
            Guild g = event.getGuild();
            TicketManager tm = new TicketManager(u, g);
            TextChannel ticketChannel;
            EmbedBuilder eb = new EmbedBuilder();
            try{
                ticketChannel = tm.create(event.getJDA());
                eb.setColor(Color.red);
                eb.setTitle(ConfigUtils.getString("Embeds.ticketClose.title"));
                eb.setDescription(ConfigUtils.getString("Embeds.ticketClose.desc"));
                eb.setFooter(ConfigUtils.getString("Embeds.ticketClose.footerText"), ConfigUtils.getString("Embeds.ticketClose.footerIcon"));
                ticketChannel.sendMessageEmbeds(eb.build()).setActionRow(Button.of(ButtonStyle.DANGER, "ticketclose", "Close ticket", Emoji.fromUnicode("U+1F6AB"))).queue();
                ticketChannel.sendMessage(u.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
                for(String s : ConfigUtils.getStringList("Tickets.supportRoles")){
                    ticketChannel.sendMessage("<@&" + s + ">").queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
                }
                logger.info(u.getUser().getAsTag() + " just opened ticket!");
            }catch (UserHaveTicketException e){
                eb.setColor(Color.RED);
                eb.setDescription(":no_entry: Sorry, but you already have ticket open, you can go to your ticket by clicking\n" + Objects.requireNonNull(event.getJDA().getTextChannelById(Bot.getInstance().
                        getTickets().get(u.getIdLong()))).getAsMention());
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                return;
            }
            event.deferEdit().queue();
        }
        if(Objects.equals(event.getButton().getId(), "ticketclose")){
            if(Bot.getInstance().getTickets().containsValue(event.getChannel().getIdLong())){
                long issuerId = 0;
                for(Map.Entry<Long, Long> e: Bot.getInstance().getTickets().entrySet()){
                    if(e.getValue() == event.getChannel().getIdLong()){
                        issuerId = e.getKey();
                    }
                }
                event.getChannel().delete().queue();
                Bot.getInstance().getTickets().remove(issuerId);
                logger.info(event.getUser().getAsTag() + " just closed ticked opened by " + Objects.requireNonNull(event.getJDA().getUserById(issuerId)).getAsTag());

            }
        }
    }
}
