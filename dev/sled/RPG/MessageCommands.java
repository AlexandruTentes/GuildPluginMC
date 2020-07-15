package dev.sled.RPG;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import dev.sled.RPG.util.Effects;
import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.VaultFunctions;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;

/**

* This class holds the chat message commands logic.

* @version 1.0
* @author Sled
* @since 2020-5-25

*/
public class MessageCommands
{
	private Storage store = new Storage();
	private PlayerLoadData pld = new PlayerLoadData();
	private Effects effect = new Effects();
	
	/**
	 * Setters.
	 * 
	 * Function which prints the player career
	 * 
	 * @param String
	 * @param PlayerData
	 * @param Player
	 */
	private void print_career(String name, PlayerData pd, Player sender)
	{
		String message = "";
		
		message += ChatColor.DARK_AQUA;
		message += name + "'s career \n";
		message += "------------------------------------------------";
		message += "\nLevel - " + pd.get_player_level().get_level();
		message += "\nGuild - " + pd.get_player_guild().get_guild_name();
		message += "\nMobs killed - " + Math.round(pd.get_mobs_killed());
		message += "\nPlayers killed - " + Math.round(pd.get_players_killed());
		message += "\nTotal damage dealt - " + Math.round(pd.get_damage_dealt());
		message += "\nTotal XP earnt - " + Math.round(pd.get_total_xp());
		message += "\nHighest mob kill streak - " + Math.round(pd.get_highest_mob_kill_streak());
		message += "\nHighest player kill streak - " + Math.round(pd.get_highest_player_kill_streak());
		
		sender.sendMessage(message);
	}
	
	public void print_stats(Player player, PlayerData pd)
	{
		effect.add_player_xp_bar(player, ChatColor.BLUE + "CURRENT XP: " + 
					Integer.toString((int) pd.get_player_level().get_current_experience()) + " -- REQUIRED XP: " +
					Integer.toString((int) pd.get_player_level().get_experience_required()), "", 7.5);
		
		String message = "";
		
		message += ChatColor.DARK_AQUA;
		message += pd.get_player_name() + "'s Stats \n";
		message += "------------------------------------------------";
		message += "\nDamage boost- " + Math.round(pd.get_player_level().get_damage());
		message += "\nLife boost - " + Integer.toString(pd.get_player_level().get_life());
		message += "\nKill streak experience multiplier per mob - " + Math.round(pd.get_kill_streak_experience_gain_multiplier() * 100 - 100) + "%";
		message += "\nExperience multiplier per mob - " + Math.round(pd.get_experience_gain_multiplier() * 100 - 100) + "%";
		message += "\nExperience multiplier total - " + Math.round(pd.get_experience_multiplier() * 100 - 100) + "%";
		
		player.sendMessage(message);
	}
	
	/**
	 * Setters.
	 * 
	 * function which displays this/other's player career info.
	 * 
	 * @param String[]
	 * @param Player
	 * @param Player
	 */
	public void msg_career(String [] command, Player player, Player sender)
	{
		PlayerLoadData pld = new PlayerLoadData();
		PlayerData pd = pld.get_player_data(player);
		
		if(command.length == 1)
		{
			print_career(player.getName(), pd, sender);
		}
		else if(command.length == 2)
		{
			@SuppressWarnings("deprecation")
			OfflinePlayer pl = Bukkit.getServer().getOfflinePlayer(command[1]);
			
			if(!pl.hasPlayedBefore() && !pl.isOnline())
			{
				sender.sendMessage(ChatColor.DARK_RED + "Player '" + pl.getName() + "' has not played before!");
				return;
			}
			
			if(Bukkit.getPlayer(command[1]) != null)
			{
				pl = (OfflinePlayer) Bukkit.getPlayer(command[1]);
				
				print_career(pl.getName(), pld.get_player_data((Player) pl), sender);
			}
			else
				print_career(pl.getName(), store.load_data(pl.getName(), pl.getUniqueId()), sender);
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which enables the leveling system.
	 * 
	 * @param Player
	 */
	public void msg_enable_leveling_system(Player sender)
	{
		if(sender.hasPermission("gp.enable.leveling.system"))
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "ENABLED LEVELING SYSTEM");
			store.set_data_config("Plugin.Enable_Leveling_System", true);
			store.add_effects();
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which disables the leveling system.
	 * 
	 * @param Player
	 */
	public void msg_disable_leveling_system(Player player, Player sender)
	{
		if(sender.hasPermission("gp.disable.leveling.system"))
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "DISABLED LEVELING SYSTEM");
			store.set_data_config("Plugin.Enable_Leveling_System", false);
			
			store.save_all_data();
			store.remove_effects(player);
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which check if the /manuadd command from groups permissions
	 * has been used. in such case, change the player's GuildPlugin level
	 * based on the newly changed (via /manuadd) group level
	 * 
	 * @param String[]
	 * @param Player
	 */
	public void msg_manuadd_command_handler(String [] command, Player sender)
	{
		if(sender.hasPermission("gp.op.permission"))
			if(command.length == 3)
			{
				@SuppressWarnings("deprecation")
				OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
				
				if(other_player instanceof Player || other_player instanceof OfflinePlayer)
				{
					int level = 0;
					int max_level = Storage.start_player_max_level;
					
					try
					{
						level = Integer.parseInt(command[2].toLowerCase().split("level")[1]);
					}
					catch(Exception err)
					{
						return;
					}
					
					if(level <= 0 || level > max_level)
					{
						sender.sendMessage(ChatColor.DARK_RED + "Valid range is between 1 and " + max_level);
						return;
					}
					
					if(!other_player.hasPlayedBefore() && !other_player.isOnline())
					{
						sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
						return;
					}
					
					(new Level()).change_level(other_player.getName(), other_player.getUniqueId(), level, sender);
					
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "DATA SET SUCCESSFULLY");
				}
			}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which changes the player level according 
	 * to the leveling system rules (aka has limits and 
	 * level checks and keeps an eye on the leveling 
	 * effects).
	 * 
	 * @param String[]
	 * @param Player
	 */
	public void msg_change_level(String [] command, Player sender)
	{
		if(sender.hasPermission("gp.change.level.permission"))
			if(command.length == 3)
			{
				@SuppressWarnings("deprecation")
				OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
				
				if(other_player instanceof Player || other_player instanceof OfflinePlayer)
				{
					int level = 0;
					int max_level = Storage.start_player_max_level;
					VaultFunctions vf = new VaultFunctions();
					Permission per = vf.get_permission();
					
					try
					{
						level = Integer.parseInt(command[2]);
					}
					catch(Exception err)
					{
						return;
					}
					
					if(level <= 0 || level > max_level)
					{
						sender.sendMessage(ChatColor.DARK_RED + "Valid range is between 1 and " + max_level);
						return;
					}
					
					if(!other_player.hasPlayedBefore() && !other_player.isOnline())
					{
						sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
						return;
					}
					
					(new Level()).change_level(other_player.getName(), other_player.getUniqueId(), level, sender);
					
					if(Bukkit.getPlayer(command[1]) != null)
						if(per.getPlayerGroups((Player)other_player)[0].split("Level").length != 1)
						{
							per.playerRemoveGroup((Player)other_player, per.getPlayerGroups((Player)other_player)[0]);
							per.playerAddGroup((Player)other_player, "Level" + Integer.toString(level));
						}
					
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "DATA SET SUCCESSFULLY");
				}
			}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which changes the current xp of a player in a
	 * controlled manner (based on the leveling system rules)
	 * 
	 * @param String[]
	 * @param Player
	 * @param Player
	 */
	public void msg_change_current_xp(String [] command, Player player, Player sender)
	{
		if(sender.hasPermission("gp.change.current.xp.permission"))
			if(command.length == 3)
			{
				@SuppressWarnings("deprecation")
				OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
				
				if(other_player instanceof Player || other_player instanceof OfflinePlayer)
				{
					PlayerLoadData pld = new PlayerLoadData();
					double xp = 0;
					
					try
					{
						xp = Double.parseDouble(command[2]);
					}
					catch(Exception err)
					{
						return;
					}
					
					if(xp <= 0 || xp > Math.pow(2, 32))
					{
						sender.sendMessage(ChatColor.DARK_RED + "Valid range is between 1 and " + Math.round(Math.pow(2, 32)));
						return;
					}
					
					if(!other_player.hasPlayedBefore() && !other_player.isOnline())
					{
						sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
						return;
					}
					
					PlayerData other_pd;
					
					if(pld.is_online(other_player.getName()))
						other_pd = pld.get_player_data(other_player.getName());
					
					else
						other_pd = store.load_data(other_player.getName(), other_player.getUniqueId());
					
					Level lvl = other_pd.get_player_level();
					
					xp = (new LevelingSystem()).level_up(xp, player);
					
					lvl.set_current_experience(xp);
					
					if(pld.is_online(other_player.getName()))
						pld.set_player_data(other_player.getName(), other_pd);
					
					else if (other_player.hasPlayedBefore())
						store.save_data(other_player.getUniqueId(), other_pd);
					
					else if(sender != null)
					{
						sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
						return;
					}
					
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "DATA SET SUCCESSFULLY");
				}
			}
	}
	
	/**
	 * Setters.
	 */
	public void msg_butcher_command_handler(Player player)
	{
		List<Entity> entity_arr;
		Monster monster = null;
		int radius = 24;
		
		entity_arr = player.getNearbyEntities(radius, radius, radius);
		
		for(Entity i: entity_arr)
		{						
			if(i instanceof LivingEntity && i instanceof Monster)
			{
				monster = (Monster) i;
				
				if(monster.getCustomName().startsWith("Level"))
					monster.remove();
			}
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which handles the /heal command
	 * so that it will not remove the positive effects
	 * of the target player.
	 * 
	 * @param Player
	 */
	public void msg_heal_command_handler(String [] command, Player player)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, new Runnable()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				try
				{
					Player pl = player;
					PlayerLoadData pld = new PlayerLoadData();
					
					if(command.length == 2)
					{
						OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
						
						pl = (Player) other_player;
					}

					if(pl.getHealth() == pl.getMaxHealth())
					{
						store.add_effects(pl);
						pld.get_player_data(pl).set_current_health(pl.getMaxHealth());	
						pl.setHealth(pl.getMaxHealth());
					}
				}
				catch(Exception e)
				{}
			}
		}, 1L);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which prints the /stats data.
	 * 
	 * @param String []
	 * @param Player
	 * @param Player
	 */
	public void msg_stats(String [] command, Player player, Player sender)
	{
		if(command.length == 1)
		{
			print_stats(player, pld.get_player_data(player.getUniqueId()));
		}
		else
		{
			@SuppressWarnings("deprecation")
			OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
			
			if(other_player instanceof Player || other_player instanceof OfflinePlayer)
			{
				if(!other_player.hasPlayedBefore() && !other_player.isOnline())
				{
					sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
					return;
				}
					
				PlayerData other_pd;
				
				if(pld.is_online(other_player.getName()))
					other_pd = pld.get_player_data(other_player.getName());
				
				else
					other_pd = store.load_data(other_player.getName(), other_player.getUniqueId());
				
				if(other_pd == null)
				{
					sender.sendMessage(ChatColor.DARK_RED + "There is no such player on the server!");
					return;
				}
				
				print_stats(player, other_pd);
			}
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player data (level, current xp, multiplier, etc).
	 * It sets it in raw values, without a regard to leveling system limits or rules.
	 * 
	 * @param String []
	 * @param Player
	 */
	public void msg_player_set(String [] command, Player sender)
	{
		int i = 0;
		
		if(sender.hasPermission("gp.change.current.xp.permission"))
		{
			if(command.length != 4)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Exactly 3 argument is needed! <TARGET> <COMMAND> <DATA>");
				return;
			}
			
			@SuppressWarnings("deprecation")
			OfflinePlayer other_player = Bukkit.getServer().getOfflinePlayer(command[1]);
			
			if(other_player instanceof Player || other_player instanceof OfflinePlayer)
			{
				PlayerData other_pd;
				
				if(pld.is_online(other_player.getName()))
					other_pd = pld.get_player_data(other_player.getName());
				else
					other_pd = store.load_data(other_player.getName(), other_player.getUniqueId());
				
				try
				{
					i = Integer.parseInt(command[3]);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.DARK_RED + "Could not convert to a number! Make sure the 2nd argument is a decimal number.");
					return;
				}
				
				if(command[2].equals("experience"))
					other_pd.get_player_level().set_current_experience(i);
				else if(command[2].equals("level"))
					other_pd.get_player_level().set_level(i);
				else if(command[2].equals("multiplier"))
					other_pd.set_experience_gain_multiplier(Double.parseDouble(command[3]));
				else
					sender.sendMessage(ChatColor.DARK_RED + "The known commands are: <COMMAND> -> 'experience', 'level', 'multiplier'");
		
				if(pld.is_online(other_player.getName()))
					pld.set_player_data(other_player.getName(), other_pd);
				else if (other_player.hasPlayedBefore())
					store.save_data(other_player.getUniqueId(), other_pd);
				else if(sender != null)
				{
					sender.sendMessage(ChatColor.DARK_RED + "Player '" + other_player.getName() + "' has not played before!");
					return;
				}
				
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "DATA SET SUCCESSFULLY");
			}
		}
	}
}
