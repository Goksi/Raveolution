package tech.goksi.raveolution;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tech.goksi.raveolution.events.Advertise;
import tech.goksi.raveolution.sql.Database;
import tech.goksi.raveolution.sql.SQLConnection;
import tech.goksi.raveolution.utils.Config;
import tech.goksi.raveolution.utils.ConfigUtils;

import javax.security.auth.login.LoginException;

public class Bot {
    private String token;
    private String guildId;
    private String ownerId;
    private final Config conf;
    private static Bot instance;
    private SQLConnection sql;
    private JDA jda;
    private Database db;
    private boolean editCfg = false;
    public Bot(){
        this.conf = new Config();
        instance = this;
        conf.saveDefaultConfig();
    }


    public void start(){
        sql = new SQLConnection();
        sql.connect();
        db = new Database();
        db.createTable();
        /*start of bot initialization*/
        JDABuilder jdaB = JDABuilder.createDefault(token);
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
        /*end of bot initialization*/

        /*start of commands*/
        /*end of commands*/
        CommandClient client = builder.build();
        jdaB.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try{
            jda = jdaB.build();
        }catch (LoginException e){
            System.out.println("[ERROR] Wrong bot token!");
            System.out.println("[ERROR] Exiting app :(");
            System.exit(1);
        }
        jda.addEventListener(client, new Advertise());
        try{
            getJda().awaitReady();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(editCfg){
            System.out.println("[Raveolution] Please make sure to edit config.yml with your info!");
        }

    }

    public static Bot getInstance(){
        return instance;
    }

    public Config getConf() {
        return conf;
    }
    public Database getDatabase(){
        return db;
    }
    public SQLConnection getSql(){
        return sql;
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
    public void setEditCfg(boolean b){
        this.editCfg = b;
    }
}
