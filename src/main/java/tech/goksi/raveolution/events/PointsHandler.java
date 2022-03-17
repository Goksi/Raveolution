package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.raveolution.Bot;
import tech.goksi.raveolution.utils.ConfigUtils;
import tech.goksi.raveolution.utils.MillisConvert;

import java.util.*;

public class PointsHandler extends ListenerAdapter {
    private final List<User> onCoolDown = new ArrayList<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!onCoolDown.contains(event.getAuthor())){
            Bot.getInstance().getDatabase().addXP(event.getAuthor());
            if(Bot.getInstance().getDatabase().getXP(event.getAuthor()) % Float.parseFloat(ConfigUtils.getString("XPSystem.xpForLvLUp")) == 0){
                Bot.getInstance().getDatabase().lvlUp(event.getAuthor());
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
            }catch (NumberFormatException e){
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
