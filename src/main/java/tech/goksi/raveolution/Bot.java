package tech.goksi.raveolution;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tech.goksi.raveolution.utils.Config;
import tech.goksi.raveolution.utils.ConfigUtils;

import javax.security.auth.login.LoginException;

public class Bot {
    private String token;
    private String guildId;
    private String ownerId;
    private final Config conf;
    private static Bot instance;
    private JDA jda;
    public Bot(){
        this.conf = new Config();
        instance = this;
        conf.saveDefaultConfig();
    }


    public void start(){


        /*start of bot initialization*/
        JDABuilder jdab = JDABuilder.createDefault(token);
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.forceGuildOnly(guildId);
        builder.setOwnerId(ownerId);
        switch (ConfigUtils.getString("BotStatus.status")){
            case "DND":
                builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
                break;
            case "IDLE":
                builder.setStatus(OnlineStatus.IDLE);
                break;
            case "INVISIBLE":
                builder.setStatus(OnlineStatus.INVISIBLE);
                break;
            default:
                builder.setStatus(OnlineStatus.ONLINE);
        }
        if(ConfigUtils.getBoolean("BotStatus.enableActivity")){
            switch (ConfigUtils.getString("BotStatus.activity")){
                case "PLAYING":
                    builder.setActivity(Activity.playing(ConfigUtils.getString("BotStatus.activityName")));
                    break;
                case "LISTENING":
                    builder.setActivity(Activity.listening(ConfigUtils.getString("BotStatus.activityName")));
                    break;
                case "WATCHING":
                    builder.setActivity(Activity.watching(ConfigUtils.getString("BotStatus.activityName")));
                    break;
                case "STREAMING":
                    builder.setActivity(Activity.streaming(ConfigUtils.getString("BotStatus.activityName"), ""));
                    break;
            }
        }
        /*start of commands*/
        /*end of commands*/
        CommandClient client = builder.build();
        jdab.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try{
            jda = jdab.build();
        }catch (LoginException e){
            System.out.println("[ERROR] Wrong bot token!");
            System.out.println("[ERROR] Exiting app :(");
            System.exit(1);
        }
        jda.addEventListener(client);
        try{
            getJda().awaitReady();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public static Bot getInstance(){
        return instance;
    }

    public Config getConf() {
        return conf;
    }

    public void setToken(String token){
        this.token = token;
    }
    public void setGuildId(String id){
        this.guildId = id;
    }
    public void setOwnerId(String id){
        this.ownerId = id;
    }
    public JDA getJda(){
        return jda;
    }
}
