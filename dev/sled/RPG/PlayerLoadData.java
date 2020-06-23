package dev.sled.RPG;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.milkbowl.vault.permission.Permission;

import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.UtilityThreads;
import dev.sled.RPG.util.VaultFunctions;

/**
* The class handles the player data loading logic (on player join server) 
* of vault permissions, chat and economy and basic player data (name, guild etc).
*
* @version 1.0
* @author Sled
* @since 2019-11-23
*/
public final class PlayerLoadData
{
	private static Storage store = null;
	
	//Global map to hold the players data
	private static Map<Player, PlayerData> player_data_list = null;
	private static Map<String, Player> player_name = null;
	private static Map<UUID, Player> player_id = null;
	
	/**
	 * This is the class constructor which will 
	 * load classes and instantiate variables.
	 */
	public PlayerLoadData()
	{
		if(player_data_list == null)
			player_data_list = new HashMap<Player, PlayerData>();
		
		if(player_name == null)
			player_name = new HashMap<String, Player>();
		
		if(player_id == null)
			player_id = new HashMap<UUID, Player>();
		
		if(store == null)
			store = new Storage();
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player data if the Player is found else null.
	 * 
	 * @param Player
	 * @return PlayerData
	 */
	public PlayerData get_player_data(Player player)
	{		
		if(!player_data_list.containsKey(player))
			return null;
		
		return player_data_list.get(player);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player data if the Player is found else null.
	 * 
	 * @param Player
	 * @return PlayerData
	 */
	public PlayerData get_player_data(String name)
	{		
		if(!player_name.containsKey(name))
			return null;
		
		if(!player_data_list.containsKey(player_name.get(name)))
			return null;
		
		return player_data_list.get(player_name.get(name));
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player data if the Player is found else null.
	 * 
	 * @param UUID
	 * @return PlayerData
	 */
	public PlayerData get_player_data(UUID id)
	{		
		if(!player_id.containsKey(id))
			return null;
		
		if(!player_data_list.containsKey(player_id.get(id)))
			return null;
		
		return player_data_list.get(player_id.get(id));
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player object if the Player name is found else null.
	 * 
	 * @param String
	 * @return Player
	 */
	public Player get_player(String name)
	{		
		if(!player_name.containsKey(name))
			return null;
		
		return player_name.get(name);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player object if the Player name is found else null.
	 * 
	 * @param UUID
	 * @return Player
	 */
	public Player get_player(UUID id)
	{		
		if(!player_id.containsKey(id))
			return null;
		
		return player_id.get(id);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns a map of all player data.
	 * 
	 * @return Map<Player, PlayerData>
	 */
	public Map<Player, PlayerData> get_all_player_data()
	{
		return player_data_list;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which checks if a given player is still online
	 * 
	 * @param Player
	 * @return boolean
	 */
	public boolean is_online(Player player)
	{
		return player_data_list.containsKey(player);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which checks if a given player is still online
	 * 
	 * @param String
	 * @return boolean
	 */
	public boolean is_online(String name)
	{
		return player_name.containsKey(name);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which checks if a given player is still online
	 * 
	 * @param UUID
	 * @return boolean
	 */
	public boolean is_online(UUID id)
	{
		return player_id.containsKey(id);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which puts a player and its data in the map
	 * 
	 * @param Player, PlayerData
	 */
	public void set_player_data(Player key, PlayerData value)
	{
		player_data_list.put(key, value);
		player_name.put(key.getName(), key);
		player_id.put(key.getUniqueId(), key);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which puts a player and its data in the map
	 * 
	 * @param String, PlayerData
	 */
	public void set_player_data(String name, PlayerData value)
	{
		if(!player_name.containsKey(name))
			return;
		
		player_data_list.put(player_name.get(name), value);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which puts a player and its data in the map
	 * 
	 * @param UUID, PlayerData
	 */
	public void set_player_data(UUID id, PlayerData value)
	{
		if(!player_id.containsKey(id))
			return;
		
		player_data_list.put(player_id.get(id), value);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which removes a player and its data in the map
	 * 
	 * @param Player
	 */
	public void remove_player_data(Player key)
	{
		if(!player_data_list.containsKey(key))
			return;
		
		player_data_list.remove(key);
		
		if(!player_name.containsKey(key.getName()))
			return;
		
		player_name.remove(key.getName());
		
		if(!player_id.containsKey(key.getUniqueId()))
			return;
					
		player_id.remove(key.getUniqueId());
	}
	
	/**
	 * Setters.
	 * 
	 * Function which removes a player from the player name map
	 * 
	 * @param String
	 */
	public void remove_player(String name)
	{
		if(!player_name.containsKey(name))
			return;
		
		player_name.remove(name);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which removes a player from the player id map
	 * 
	 * @param UUID
	 */
	public void remove_player(UUID id)
	{
		if(!player_id.containsKey(id))
			return;
		
		player_id.remove(id);
	}
	
	private void load_leveling_system(Player player)
	{
		PlayerData pd = null;
		VaultFunctions vf = new VaultFunctions();
		Permission p = vf.get_permission();
		UtilityThreads ut = new UtilityThreads(player);

		//---Set this to false to get to the database
		if(!player.hasPlayedBefore())
		{
			pd = new PlayerData(
				player.getName(), new Guild(), new Party(), new Quest(),
				new Level(Storage.start_player_experience, 
						  Storage.start_player_experience_required, 
						  Storage.start_player_level), 
						  Storage.start_player_experience_required_multiplier, 
						  Storage.database_version);		
		}
		else
			//Load the data from the database			
			pd = store.load_data(player.getName(), player.getUniqueId());
		
		if(p.getPlayerGroups(player)[0].split("Level").length != 1)
		{
			p.playerRemoveGroup(player, p.getPlayerGroups(player)[0]);
			p.playerAddGroup(player, "Level" + Integer.toString(pd.get_player_level().get_level()));
		}
		
		double xp_req = Math.round(Storage.start_player_experience_required * Math.pow(
				Storage.start_player_experience_required_multiplier + 1, pd.get_player_level().get_level() - 1));
		
		pd.get_player_level().set_experience_required(xp_req < Storage.define_MAX_XP_REQ ? xp_req : Storage.define_MAX_XP_REQ);
		
		if(pd.get_player_level().get_current_experience() >= pd.get_player_level().get_experience_required())
			pd.get_player_level().set_current_experience(0.75 * pd.get_player_level().get_experience_required());
		
		pd.get_player_level().set_life(pd.get_player_level().get_level() / 10);
		
		set_player_data(player, pd);
		ut.start_thread(ut.each_player_thread, ut.each_player_runnable);
		
		//store.remove_effects(player);
		store.add_effects(player);
	}
	
	private void unload_leveling_system(Player player)
	{		
		UtilityThreads ut = new UtilityThreads();
		PlayerData pd = get_player_data(player);
		
		store.save_data(player.getUniqueId(), pd);
		
		remove_player_data(player);
		ut.stop_player_thread(player);
	}
	
	/**
	 * Event
	 * 
	 * Function which is called by a player join event
	 * 
	 * @param event
	 */
	public void on_join(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		// Load player leveling system data
		load_leveling_system(player);
	}
	
	/**
	 * Save player's data to the server
	 * 
	 * @param event
	 */
	@EventHandler
	public void on_disconnect(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		
		// Unload player leveling system data
		unload_leveling_system(player);
	}
	
	/**
	 * Reload player's data on the server
	 * 
	 * @param event
	 */
	@EventHandler
	public void on_respawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				store.add_effects(player);
			}
		}, 5L);
	}
	
	/**
	 * Event
	 * 
	 * Function which is called by a player command message event
	 * Used to print and test this class
	 * 
	 * @param event
	 */
	public void on_player_message(PlayerCommandPreprocessEvent event)
	{
		Player sender = event.getPlayer();
		String [] command = event.getMessage().split(" ");
		MessageCommands msgc = new MessageCommands();
		
		if(!(sender instanceof Player))
			return;
		
		Player player = (Player) sender;
		
		switch(command[0])
		{
			case "/stats": msgc.msg_stats(command, player, sender); break;
			case "/gp_player_set": msgc.msg_player_set(command, sender); break;
		}
	}
}
