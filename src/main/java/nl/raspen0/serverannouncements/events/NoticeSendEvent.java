package nl.raspen0.serverannouncements.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class NoticeSendEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private int count;
    private final NoticeType type;
    private boolean cancelled;

    public NoticeSendEvent(Player player, int count, NoticeType type) {
        super(player);
        this.count = count;
        this.type = type;
    }

    public NoticeType getNoticeType() {
        return type;
    }

    public int getUnreadCount() {
        return count;
    }

    public void setUnreadCount(int count) {
        this.count = count;
    }

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
