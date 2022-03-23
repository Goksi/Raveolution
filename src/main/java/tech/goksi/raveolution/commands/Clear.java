
package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.utils.ConfigUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Clear extends SlashCommand {
    public Clear(){
        this.name = "clear";
        this.cooldown = 30;
        this.defaultEnabled = false;
        this.help = "Clears given number of messages";
        List<String> roles = ConfigUtils.getStringList("Clear.allowedRanks");
        this.enabledRoles = roles.toArray(new String[0]);
        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "messages", "Number of messages to delete").setRequired(true));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        OptionMapping option = event.getOption("messages");
        assert option != null;
        int messages = option.getAsInt();
        List<Message> forDelete = event.getChannel().getHistory().retrievePast(messages).complete();
        event.getTextChannel().deleteMessages(forDelete).queue();
        event.reply(Objects.requireNonNull(ConfigUtils.getString("Clear.messageSuccess")).
                replaceAll("%number%", String.valueOf(messages))).queue(msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));

    }
}