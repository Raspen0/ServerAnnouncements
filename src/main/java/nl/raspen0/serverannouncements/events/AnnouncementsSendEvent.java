package nl.raspen0.serverannouncements.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnouncementsSendEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
//    private Collection<?> announcements;
    private boolean cancelled;

    public AnnouncementsSendEvent(Player player) {
        super(player);
//        this.announcements = announcements;
    }

//    public Collection<?> getMessages(){
//        return announcements;
//    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
