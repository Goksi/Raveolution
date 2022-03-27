package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class Private extends SlashCommand {
    public Private(){
        this.name = "private";
        this.help = "Options for your private channel";
        this.defaultEnabled = true;
        this.children = new SlashCommand[]{new Add(), new Remove()};
    }
    @Override
    protected void execute(SlashCommandEvent event) {
      /*Nothing to see here :(*/

    }
}

class Add extends SlashCommand{
    public Add(){
        this.name = "add";
        this.help = "Adds user to your private channel";
        this.defaultEnabled = true;
        this.cooldown = 10;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "User you want to add to your private channel").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User target = event.optUser("user");

    }
}
class Remove extends SlashCommand{
    public Remove(){
        this.name = "remove";
        this.help = "Removes user from your private channel";
        this.defaultEnabled = true;
        this.cooldown = 10;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "User you want to remove from private channel").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User target = event.optUser("user");

    }
}