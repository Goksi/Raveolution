package tech.goksi.raveolution.events;

import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.raveolution.Bot;

import java.util.Map;
import java.util.Objects;

public class ShutdownHandler extends ListenerAdapter {
    @Override
    /*not fired ?*/
    public void onShutdown(@NotNull ShutdownEvent event) {
        Bot.getInstance().getSql().disconnect();
        if(!Bot.getInstance().getTickets().isEmpty()){
            for(Map.Entry<Long, Long> e : Bot.getInstance().getTickets().entrySet()){
                Objects.requireNonNull(Bot.getInstance().getJda().getTextChannelById(e.getValue())).delete().queue();
            }
        }

    }
}
