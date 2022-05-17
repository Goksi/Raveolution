package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;


import java.awt.*;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SetupTicket extends SlashCommand {
    public SetupTicket(){
        this.name = "setupticket";
        this.help = "Sends embed with button to open ticket in the channel";
        this.cooldown = 30;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "category", "Category id for your ticket channels").setRequired(true));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String categoryId = Objects.requireNonNull(event.optString("category"));
        net.dv8tion.jda.api.entities.Category category;
        try{
            category = event.getJDA().getCategoryById(categoryId);
        }catch (NumberFormatException e){
            invalidEmbed(eb, event);;
            return;
        }
        if(category == null) {
            invalidEmbed(eb, event);
            return;
        }
        Bot.getInstance().getConf().getValues().set("Tickets.ticketsCategoryId", category.getId());
        Bot.getInstance().getConf().saveConfig();
        event.reply("fck discord").queue(msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS)); //literally know no better way to acknowledge this interaction >.<
        eb.setTitle(ConfigUtils.getString("Embeds.ticket.title"));
        eb.setColor(Color.RED);
        eb.setDescription(ConfigUtils.getString("Embeds.ticket.desc"));
        eb.setFooter(ConfigUtils.getString("Embeds.ticket.footerText"), ConfigUtils.getString("Embeds.ticket.footerIcon"));
        event.getTextChannel().sendMessageEmbeds(eb.build()).setActionRow(Button.of(ButtonStyle.SUCCESS, "ticket", "Submit a ticket!", Emoji.fromUnicode("U+1F4E9"))).queue();
    }
    private void invalidEmbed(EmbedBuilder eb, SlashCommandEvent e){
        eb.setColor(Color.RED);
        eb.setDescription(":no_entry: Invalid category id, try again!");
        e.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
}

