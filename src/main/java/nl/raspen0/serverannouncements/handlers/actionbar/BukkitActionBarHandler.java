package nl.raspen0.serverannouncements.handlers.actionbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BukkitActionBarHandler extends ActionBarHandler{

    private final ServerAnnouncements plugin;

    public BukkitActionBarHandler(ServerAnnouncements plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    void doActionBarTask(Player player, String count){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(plugin.getLangHandler()
                .getMessage("noticeHotBar").replace("{0}", count)));
        try {
            manager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
