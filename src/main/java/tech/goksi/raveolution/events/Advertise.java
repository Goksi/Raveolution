package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.utils.ConfigUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Advertise extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(Advertise.class.getName());
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event){
        if(!ConfigUtils.getBoolean("Advertising.allow")){
            if(!event.getMessage().getInvites().isEmpty()){
                List<String> invites = event.getMessage().getInvites();
                if(Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) return;
                for(String inv : invites){
                    Invite.resolve(event.getJDA(), inv).queue(invite -> {
                        if(invite.getGuild() == null) return;
                        if(!invite.getGuild().getId().equals(event.getGuild().getId())){
                            logger.info("User " + event.getAuthor().getAsTag() + " just tried to advertise !");
                            String message = event.getMessage().getContentRaw();
                            event.getMessage().delete().queue();
                            if(ConfigUtils.getBoolean("Advertising.dmAdvertiser")){
                                event.getAuthor().openPrivateChannel().flatMap(privateChannel ->
                                        privateChannel.sendMessage(ConfigUtils.getString("Advertising.message", "%user%", event.getMember().getAsMention()))).queue();

                            }else {
                                event.getChannel().sendMessage(ConfigUtils.getString("Advertising.message", "%user%", event.getMember().getAsMention())).queue();
                            }
                            TextChannel logs = event.getGuild().getTextChannelById(ConfigUtils.getString("Logs.logsChannelId"));
                            assert logs != null;
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Advertising attempt!");
                            eb.setColor(Color.RED);
                            eb.addField("User: ", event.getAuthor().getAsMention(), false);
                            eb.addField("Channel: ", event.getChannel().getAsMention(), false);
                            eb.addField("Message: ", "`" + message +"`", false);
                            eb.setTimestamp(new Date().toInstant());
                            logs.sendMessageEmbeds(eb.build()).queue();

                        }
                    }, throwable -> {
                    });
                }

            }
        }
    }
}
