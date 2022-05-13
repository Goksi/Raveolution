package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import tech.goksi.raveolution.utils.ConfigUtils;


import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SetupTicket extends SlashCommand {
    public SetupTicket(){
        /*TODO: dodati da mozes da stavis id ili channel*/
        this.name = "setupticket";
        this.help = "Sends embed with button to open ticket in the channel";
        this.cooldown = 30;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        if(ConfigUtils.getString("Tickets.ticketsCategoryId").equals("Put your category id") || event.getJDA().getCategoryById(ConfigUtils.getString("Tickets.ticketsCategoryId")) == null){
            eb.setColor(Color.RED);
            eb.setDescription(":no_entry: Please put valid category id in config.yml file");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        event.reply("fck discord").queue(msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS)); //literally know no better way to acknowledge this interaction >.<
        eb.setTitle(ConfigUtils.getString("Embeds.ticket.title"));
        eb.setColor(Color.RED);
        eb.setDescription(ConfigUtils.getString("Embeds.ticket.desc"));
        eb.setFooter(ConfigUtils.getString("Embeds.ticket.footerText"), ConfigUtils.getString("Embeds.ticket.footerIcon"));
        event.getTextChannel().sendMessageEmbeds(eb.build()).setActionRow(Button.of(ButtonStyle.SUCCESS, "ticket", "Submit a ticket!", Emoji.fromUnicode("U+1F4E9"))).queue();
    }
}
