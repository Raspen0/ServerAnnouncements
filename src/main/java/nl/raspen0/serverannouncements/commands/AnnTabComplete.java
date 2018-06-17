package nl.raspen0.serverannouncements.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnTabComplete implements TabCompleter {

    //TODO: AutoComplete Announcement titles.

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1){
            List<String> filteredList = new ArrayList<>();
            filteredList.add("read");
            if(sender.hasPermission("serverann.admin")){
                filteredList.add("admin");
            }
            StringUtil.copyPartialMatches(args[0], filteredList, list);
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("admin")){
               if(!sender.hasPermission("serverann.admin")){
                   return null;
               }
                StringUtil.copyPartialMatches(args[1], Arrays.asList("create", "delete", "info", "list", "modify", "reload", "show"), list);
            }
        }
        if(args.length == 3){
            if(args[0].equalsIgnoreCase("admin")) {
                if (!sender.hasPermission("serverann.admin")) {
                    return null;
                }
                if(args[1].equalsIgnoreCase("modify")){
                    StringUtil.copyPartialMatches(args[2], Arrays.asList("permission", "text", "title"), list);
                }
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }
}
