package com.argo.bukkit.util;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;
import com.mcbans.firestar.mcbans.BukkitInterface;
import com.mcbans.firestar.mcbans.pluginInterface.Ban;
import com.mcbans.firestar.mcbans.pluginInterface.Kick;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.cole2sworld.colebans.ColeBansPlugin;
import com.cole2sworld.colebans.framework.PlayerAlreadyBannedException;
import com.cole2sworld.colebans.framework.PlayerOfflineException;

/**
 * This class was originally all static methods (arrrrg).. Refactored to be an
 * instance so we can
 * pass in instance variables such as the Config object we're using. It really
 * should be re-written
 * to use an Interface and each Ban type (mcbans, easyban, etc) should be their
 * own implementation
 * of the interface and let polymorphism determine which one is used at run-time
 * as opposed to a
 * switch statement. #OOPftw
 * 
 * @author Argomirr, morganm
 * 
 */
public class BansHandler {
	
	@SuppressWarnings("unused")
	private final Honeypot	plugin;
	private final Config	config;
	private BukkitInterface	mcb3;
	private BansMethod		bmethod	= BansMethod.VANILLA;	// default
	private ColeBansPlugin	cb;
	
	public BansHandler(final Honeypot plugin) {
		this.plugin = plugin;
		config = plugin.getHPConfig();
	}
	
	public void ban(final Player p, final String sender, String reason) {
		// get player location (more useful than HP block loc.)
		if (config.getLocFlag() == true) {
			final Location loc = p.getLocation();
			final int locx = (int) loc.getX();
			final int locy = (int) loc.getY();
			final int locz = (int) loc.getZ();
			reason = reason
					+ " (" + locx + "," + locy + "," + locz + ")";
		}
		// use right ban method
		switch (bmethod) {
			case VANILLA:
				// fix for black screen after BAN
				p.kickPlayer(config.getPotMsg());
				VanillaBan(p);
				break;
			case MCBANS3:
				MCBan3(p, sender, reason, "");
				break;
			case EASYBAN:
				// also fix for black screen after BAN
				p.kickPlayer(config.getPotMsg());
				Eban(p, reason);
				break;
			case KABANS:
				KAban(p, reason);
				break;
			case UBAN:
				Uban(p, reason);
				break;
			case COLEBANS:
				Cban(p, reason);
				break;
			default:
				break;
		}
	}
	
	public void kick(final Player p, final String sender, final String reason) {
		// use right kick method
		switch (bmethod) {
			case VANILLA:
				p.kickPlayer(reason);
				break;
			case MCBANS3:
				MCBan3Kick(p, sender, reason);
				break;
			case EASYBAN:
				EBkick(p, reason);
				break;
			case KABANS:
				KAkick(p, reason);
				break;
			case UBAN:
				Ukick(p, reason);
				break;
			case COLEBANS:
				Ckick(p, reason);
				break;
			default:
				p.kickPlayer(reason);
				break;
		}
	}
	
	public BansMethod setupbanHandler(final JavaPlugin plugin) {
		// Check for ColeBans
		final Plugin testColeBans = plugin.getServer().getPluginManager().getPlugin("ColeBans");
		// Check for MCBans
		Plugin testMCBans = plugin.getServer().getPluginManager().
				getPlugin("mcbans");
		if (testMCBans == null) // Compatibility for older MCBans releases
		{
			testMCBans = plugin.getServer().getPluginManager().
					getPlugin("MCBans");
		}
		// Check for EasyBans
		Plugin testEB = plugin.getServer().getPluginManager().
				getPlugin("EasyBan");
		if (testEB == null) // Compatibility for oldEasyBan release
		{
			testEB = plugin.getServer().getPluginManager().
					getPlugin("easyban");
		}
		// Check for KiwiAdmin
		Plugin testKA = plugin.getServer().getPluginManager().
				getPlugin("KiwiAdmin");
		if (testKA == null) // Compatibility for older bad-releases
		{
			testKA = plugin.getServer().getPluginManager().
					getPlugin("kiwiadmin");
		}
		// Check for UltraBan
		Plugin testUB = plugin.getServer().getPluginManager().
				getPlugin("UltraBan");
		if (testUB == null) // Compatibility for older bad-releases
		{
			testUB = plugin.getServer().getPluginManager().
					getPlugin("ultraban");
		}
		
		if (testColeBans != null) {
			cb = (ColeBansPlugin) testColeBans;
			bmethod = BansMethod.COLEBANS;
		} else if (testMCBans != null) {
			// We only support version 3.8+ now, Dropped version test.
			mcb3 = (BukkitInterface) testMCBans;
			bmethod = BansMethod.MCBANS3;
		} else if (testEB != null) {
			bmethod = BansMethod.EASYBAN;
		} else if (testKA != null) {
			bmethod = BansMethod.KABANS;
		} else if (testUB != null) {
			bmethod = BansMethod.UBAN;
		} else {
			bmethod = BansMethod.VANILLA;
		}
		return bmethod;
	}
	
	/**
	 * @param p
	 * @param reason
	 */
	private void Cban(final Player p, final String reason) {
		try {
			cb.banHandler.banPlayer(p.getName(), reason, "[Honeypot]");
		} catch (final PlayerAlreadyBannedException e) {
			// okay then :)
		}
	}
	
	/**
	 * @param p
	 * @param reason
	 */
	private void Ckick(final Player p, final String reason) {
		try {
			cb.kickPlayer(p.getName(), reason);
		} catch (final PlayerOfflineException e) {
			e.printStackTrace();
			// how!?
		}
	}
	
	private void Eban(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"eban " + player.getName() + " " + reason);
	}
	
	private void EBkick(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"ekick " + player.getName() + " " + reason);
	}
	
	private void KAban(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"ban " + player.getName() + " " + reason);
	}
	
	private void KAkick(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"kick " + player.getName() + " " + reason);
	}
	
	private void MCBan3(final Player player, final String sender, final String reason,
			final String type) {
		player.kickPlayer(reason); // kick for good measure
		
		String banType = "localBan";
		// "localBan" or "globalBan" - need to make a config option
		if (config.isGlobalBan()) {
			banType = "globalBan";
		}
		
		final Ban banControl = new Ban(mcb3, banType, player.getName(), player.getAddress()
				.toString(), sender, reason, "", "");
		final Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}
	
	private void MCBan3Kick(final Player player, final String sender, final String reason) {
		final Kick kickControl = new Kick(mcb3.Settings, mcb3, player.getName(), sender, reason);
		final Thread triggerThread = new Thread(kickControl);
		triggerThread.start();
	}
	
	private void Uban(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"ipban " + player.getName() + " " + reason);
	}
	
	private void Ukick(final Player player, final String reason) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"kick " + player.getName() + " " + reason);
	}
	
	private void VanillaBan(final Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
				"ban " + player.getName());
	}
}
