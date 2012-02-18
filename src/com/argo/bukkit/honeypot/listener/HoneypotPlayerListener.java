package com.argo.bukkit.honeypot.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.argo.bukkit.honeypot.HoneyStack;
import com.argo.bukkit.honeypot.Honeyfarm;
import com.argo.bukkit.honeypot.Honeypot;

public class HoneypotPlayerListener implements Listener {
	private Honeypot plugin;
    private HoneyStack honeyStack;

    public HoneypotPlayerListener(Honeypot instance) {
    	plugin = instance;
    	honeyStack = plugin.getHoneyStack();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if(Honeyfarm.getPotSelect(player) && event.getAction() == Action.RIGHT_CLICK_BLOCK){
    		if(player.getItemInHand().getTypeId() == plugin.getHPConfig().getToolId() && plugin.hasPermission(player, "honeypot.create")) {
    			if(!Honeyfarm.isPot(event.getClickedBlock().getLocation())) {
    				Honeyfarm.createPot(event.getClickedBlock().getLocation());
    				player.sendMessage(ChatColor.GREEN + "Honeypot created. Destroy the block to remove the honeypot.");
    			} else {
    				player.sendMessage(ChatColor.DARK_RED + "That block is already marked as a honeypot.");
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKick(PlayerKickEvent event) {
    	honeyStack.playerLogout(event.getPlayer().getName());
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	honeyStack.playerLogout(event.getPlayer().getName());
    }
}
