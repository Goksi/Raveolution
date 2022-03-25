package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.exceptions.FailFormatException;
import tech.goksi.raveolution.utils.ConfigUtils;
import tech.goksi.raveolution.utils.MillisConvert;


import java.awt.*;
import java.util.*;
import java.util.List;

public class Mute extends SlashCommand {
    public Mute(){
        this.name = "mute";
        this.defaultEnabled = false;
        List<String> roles = ConfigUtils.getStringList("Mute.allowedRanks");
        this.enabledRoles = roles.toArray(new String[0]);
        this.help = "Mutes a specific member for a given amount of time";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "Select a user to be muted").setRequired(true));
        options.add(new OptionData(OptionType.STRING, "time", "Amount of time user will be muted for").setRequired(false));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User target = event.optUser("user");
        String time = event.optString("time");
        boolean permanent = false;
        if(ConfigUtils.getString("Mute.muteRole").equals("Put your mute role id here") || event.getJDA().getRoleById(ConfigUtils.getString("Mute.muteRole")) == null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setDescription(":no_entry: Please put a valid role into your config.yml");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        if(time == null){
            permanent = true;
        }
        Role muteRole = event.getJDA().getRoleById(ConfigUtils.getString("Mute.muteRole"));
        assert muteRole != null;
        assert target != null;
        Objects.requireNonNull(event.getGuild()).addRoleToMember(Objects.requireNonNull(event.getGuild().getMember(target)), muteRole).queue();
        if(!permanent){
            long timeLong;
            try{
                timeLong = MillisConvert.toMilli(time);
            }catch (FailFormatException e){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setDescription(":no_entry: Sorry, but you provided bad format for mute duration, should be 1d for 1 day 3h for three hours etc..");
                eb.setColor(Color.red);
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                return;
            }
            Date due = new Date(System.currentTimeMillis() + timeLong);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getGuild().getMember(target)), muteRole).queue();
                    if(ConfigUtils.getBoolean("Mute.unmuteDm")){
                        target.openPrivateChannel().flatMap(dm -> dm.sendMessage(ConfigUtils.getString("Mute.unmuteDmMessage"))).queue();
                    }
                }
            }, due);
            event.reply(ConfigUtils.getString("Mute.message", "%user%", target.getAsMention()).replaceAll("%time%", stripTime(time))).queue();

            return;
        }
        event.reply(ConfigUtils.getString("Mute.messagePermanent", "%user%", target.getAsMention())).queue();


    }

    private String stripTime(String time){
        StringBuilder sb = new StringBuilder();
        int number = time.charAt(0);
        char c = time.charAt(1);
        sb.append(number).append(" ");
        switch (c){
            case 'd' : sb.append("day");
            case 'h' : sb.append("hour");
            case 'm' : sb.append("hour");
            case 's' : sb.append("second");
        }
        return sb.toString();
    }
}
