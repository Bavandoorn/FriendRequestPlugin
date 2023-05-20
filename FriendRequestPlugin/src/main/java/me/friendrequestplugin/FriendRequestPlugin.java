package me.friendrequestplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FriendRequestPlugin extends JavaPlugin implements Listener {
    private Map<UUID, UUID> friendRequests = new HashMap<>();
    private Map<UUID, UUID> friends = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("friend")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /friend <request|accept|reject|cancel|chat> <player>");
                return true;
            }

            Player senderPlayer = (Player) sender;
            Player targetPlayer = Bukkit.getPlayer(args[1]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            UUID senderUUID = senderPlayer.getUniqueId();
            UUID targetUUID = targetPlayer.getUniqueId();

            if (args[0].equalsIgnoreCase("request")) {
                friendRequests.put(targetUUID, senderUUID);
                sender.sendMessage(ChatColor.GREEN + "Friend request sent to " + targetPlayer.getName());
                targetPlayer.sendMessage(ChatColor.YELLOW + "You received a friend request from " + senderPlayer.getName() + ". Use /friend accept " + senderPlayer.getName() + " to accept.");
            } else if (args[0].equalsIgnoreCase("accept")) {
                if (friendRequests.containsKey(senderUUID) && friendRequests.get(senderUUID).equals(targetUUID)) {
                    friendRequests.remove(senderUUID);
                    friends.put(senderUUID, targetUUID);
                    sender.sendMessage(ChatColor.GREEN + "You are now friends with " + targetPlayer.getName());
                    targetPlayer.sendMessage(ChatColor.GREEN + senderPlayer.getName() + " accepted your friend request.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have any pending friend requests from " + targetPlayer.getName());
                }
            } else if (args[0].equalsIgnoreCase("reject")) {
                if (friendRequests.containsKey(senderUUID) && friendRequests.get(senderUUID).equals(targetUUID)) {
                    friendRequests.remove(senderUUID);
                    sender.sendMessage(ChatColor.GREEN + "You rejected the friend request from " + targetPlayer.getName());
                    targetPlayer.sendMessage(ChatColor.RED + senderPlayer.getName() + " rejected your friend request.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have any pending friend requests from " + targetPlayer.getName());
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (friendRequests.containsKey(targetUUID) && friendRequests.get(targetUUID).equals(senderUUID)) {
                    friendRequests.remove(targetUUID);
                    sender.sendMessage(ChatColor.GREEN + "Canceled the friend request to " + targetPlayer.getName());
                    targetPlayer.sendMessage(ChatColor.RED + senderPlayer.getName() + " canceled the friend request.");
                } else {
                    sender.sendMessage(ChatColor.RED + "No pending friend request to " + targetPlayer.getName());
                }
            } else if (args[0].equalsIgnoreCase("chat")) {
                if (friends.containsKey(senderUUID) && friends.get(senderUUID).equals(targetUUID)) {
                    String message = String.join(" ", args).replaceFirst("chat ", "");
                    senderPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "You" + ChatColor.GRAY + " -> " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + "] " + message);
                    targetPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + senderPlayer.getName() + ChatColor.GRAY + " -> " + ChatColor.GREEN + "You" + ChatColor.GRAY + "] " + message);
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not friends with " + targetPlayer.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid command.");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    }
}
