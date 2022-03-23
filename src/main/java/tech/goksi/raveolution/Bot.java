package tech.goksi.raveolution;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.commands.*;
import tech.goksi.raveolution.events.*;
import tech.goksi.raveolution.sql.Database;
import tech.goksi.raveolution.sql.SQLConnection;
import tech.goksi.raveolution.config.Config;
import tech.goksi.raveolution.utils.ConfigUtils;


import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Integer> invitesUsage;
    private final Logger logger;



    private Map<Long, Long> tickets;
    public Bot(){
        this.conf = new Config();
        instance = this;
        conf.saveDefaultConfig();
        this.logger = LoggerFactory.getLogger(Bot.class);
    }


    public void start(){
        sql = new SQLConnection();
        sql.connect();
        db = new Database();
        db.createTable();
        invitesUsage = new HashMap<>();
        tickets = new HashMap<>();
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
        builder.addSlashCommand(new Leaderboards());
        builder.addSlashCommand(new Level());
        builder.addSlashCommand(new Clear());
        builder.addSlashCommand(new Invites());
        builder.addSlashCommand(new Cat());
        builder.addSlashCommand(new CoronaApi());
        builder.addSlashCommand(new Giveaway());
        builder.addSlashCommand(new SetupTicket());
        builder.addSlashCommand(new Avatar());
        /*end of commands*/
        CommandClient client = builder.build();
        jdaB.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try{
            jda = jdaB.build();
        }catch (LoginException e){
            logger.error("Wrong bot token!");
            logger.info("Exiting app :(");
            System.exit(1);
        }
        jda.addEventListener(client, new Advertise(), new XPHandler(), new InvitesHandler(), new ShutdownHandler(), new TicketHandler());
        try{
            getJda().awaitReady();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        addInvitesToMap();
        getDatabase().fixInvites();
        if(editCfg){
            logger.info("Please make sure to edit config.yml with your info!");
        }
        logger.info("Bot started successfully!");


    }
    /*getters and setters*/
    public static Bot getInstance(){
        return instance;
    }
    public Map<Long, Long> getTickets() {
        return tickets;
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
    public Map<String, Integer> getInvitesUsage(){
        return invitesUsage;
    }
    /*end of getters and setters*/
    private void addInvitesToMap(){
        Map<String, Integer> temp = new HashMap<>();
        if(getJda().getGuilds().get(0).retrieveInvites().complete().isEmpty()) return;
        for(Invite i : getJda().getGuilds().get(0).retrieveInvites().complete()){
            temp.put(i.getCode(), i.getUses());
        }
        invitesUsage = temp;
    }


}
