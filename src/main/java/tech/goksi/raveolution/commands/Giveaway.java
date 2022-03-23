package tech.goksi.raveolution.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.goksi.raveolution.exceptions.FailFormatException;
import tech.goksi.raveolution.utils.ConfigUtils;
import tech.goksi.raveolution.utils.MillisConvert;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Giveaway extends SlashCommand {
    public Giveaway(){
        this.name = "giveaway";
        this.cooldown = 30;
        this.defaultEnabled = false;
        List<String> roles = ConfigUtils.getStringList("Giveaway.allowedRanks");
        this.enabledRoles = roles.toArray(new String[0]);
        this.help = "Starts a giveaway in same channel command was sent!";
        List<OptionData> od = new ArrayList<>();
        od.add(new OptionData(OptionType.STRING, "duration", "For how long giveaway will last").setRequired(true));
        od.add(new OptionData(OptionType.STRING, "prize", "Prize that will be displayed in embed").setRequired(true));
        od.add(new OptionData(OptionType.BOOLEAN, "everyone", "Set true if you want to ping everyone for this gw!"));
        this.options = od;
    }
    private Message msg;
    @Override
    protected void execute(SlashCommandEvent event) {
        String duration = event.optString("duration");
        String prize = event.optString("prize");
        boolean everyone = event.optBoolean("everyone", false);
        long durationLong;
        EmbedBuilder eb = new EmbedBuilder();
        try{
            assert duration != null;
            durationLong = MillisConvert.toMilli(duration);
        }catch (FailFormatException e){
            eb.setDescription(":no_entry: Sorry, but you provided bad format for duration, should be 1d for 1 day 3h for three hours etc..");
            eb.setColor(Color.red);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        Date due = new Date(System.currentTimeMillis() + durationLong);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = sdf.format(due);
        eb.setTitle(ConfigUtils.getString("Giveaway.title"));
        eb.setColor(Color.red);
        eb.addField("Prize: ", "`" + prize + "`", false);
        eb.addField("Giveaway active till: ", "`" + date + "`", false);
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Giveaway hosted by " + event.getUser().getAsTag());
        event.deferReply().queue();
        msg = event.getHook().sendMessageEmbeds(eb.build()).complete();
        msg.addReaction("U+2705").queue();
        if(everyone){
            event.getTextChannel().sendMessage(Objects.requireNonNull(event.getGuild()).getPublicRole().getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<User> users = msg.retrieveReactionUsers("U+2705").complete();
                users.remove(event.getJDA().getSelfUser());
                if(users.isEmpty()){
                    msg.reply(ConfigUtils.getString("Giveaway.noWinner")).queue();
                    return;
                }
                int result = (int) (Math.random() * (users.size()));
                User winner = users.get(result);
                msg.getChannel().sendMessage(event.getUser().getAsMention()).queue(msg -> msg.delete().queueAfter(3, TimeUnit.SECONDS));
                msg.reply(ConfigUtils.getString("Giveaway.winner").replaceAll("%winner%", winner.getAsMention())).queue();

            }
        }, due);


    }
}
