package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.exceptions.FailFormatException;
import tech.goksi.raveolution.utils.ConfigUtils;
import tech.goksi.raveolution.utils.LevelUtils;
import tech.goksi.raveolution.utils.MillisConvert;
import java.util.*;

public class XPHandler extends ListenerAdapter {
    private final List<User> onCoolDown = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(XPHandler.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!ConfigUtils.getBoolean("XPSystem.enabled")) return;
        if(event.getAuthor().isBot()) return; //why this isn't default >.<
        if(!onCoolDown.contains(event.getAuthor())){
            long randomXP = LevelUtils.randomXp(10, 25);
            Bot.getInstance().getDatabase().addXP(event.getAuthor(), randomXP);
            int xpToLvl = LevelUtils.xpToLevels(Bot.getInstance().getDatabase().getXP(event.getAuthor()));
            if(xpToLvl > Bot.getInstance().getDatabase().getLvl(event.getAuthor())){
                Bot.getInstance().getDatabase().addLvl(event.getAuthor(), xpToLvl);
                logger.info("[Raveolution] User " + event.getAuthor().getAsTag() + " has been levelup to level " + xpToLvl);

                if(ConfigUtils.getBoolean("XPSystem.lvlUpMsg")){
                    event.getChannel().sendMessage(ConfigUtils.getString("XPSystem.lvlUpMsgStr", "%user%", event.getAuthor().getAsMention()).
                            replaceAll("%level%", String.valueOf(Bot.getInstance().getDatabase().getLvl(event.getAuthor())))).queue();
                }
            }

            onCoolDown.add(event.getAuthor());
            Timer timer = new Timer();
            long cd;
            try{
                cd = MillisConvert.toMilli(ConfigUtils.getString("XPSystem.delay"));
            }catch (FailFormatException e){
                e.printStackTrace();
                return;
            }
            Date due = new Date(System.currentTimeMillis() + cd);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onCoolDown.remove(event.getAuthor());
                }
            }, due);


        }
    }
}
