package dev.sled.RPG.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import dev.sled.RPG.Combat;
import dev.sled.RPG.MobLevel;
import dev.sled.RPG.PlayerData;
import dev.sled.RPG.PlayerLoadData;

/**

* This class handles all required threads that run in the background

* @version 1.0
* @author Sled
* @since 2020-3-27

*/
public class UtilityThreads
{
	private UUID id = null;
	private static Storage store = null;
	private double prev_damage_taken = 0.0;
	private Player player = null;
	private static PlayerLoadData pld = null;
	private static PluginLogger log = null;
	private static UtilityFunctions utils = new UtilityFunctions();
	private static MobLevel mob;
	
	// Initialize threads
	public static Map<Player, Thread> each_player_threads = null;
    public static Thread auto_save_thread;
    public static Thread garbage_collector_thread;
	public Thread combat_data_thread;
	public Thread each_player_thread;
	
	public Runnable garbage_collector_runnable = () ->
	{
		try
		{
			while(true)
			{
				Map<Player, PlayerData> map = pld.get_all_player_data();
				
				for(Map.Entry<Player, PlayerData> entry : map.entrySet())
				{
					Player player = entry.getKey();			
					
					if(!player.isOnline())
					{
						pld.remove_player_data(player);
						stop_player_thread(player);
					}
				}
				
				try
				{
					Thread.sleep(1000 * 60);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
		}
		catch(Exception exception)
		{
			if(log == null)
				return;
			
			log.print_log("'Runnable garbage_collector_runnable' exception inside UtilityThreads.java class", exception);
		}
	};
	
	 // Creating the auto save runnable
    public Runnable auto_save_runnable = () ->
	{
		try
		{
			int time = 0;
			
			while(true)
			{
				if(Storage.start_plugin_leveling_system_status)
				{
					//store.save_all_data();
				}
				
				while(time <= Storage.start_storage_auto_save)
				{
					time++;
					
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
				
				time = 0;
			}
		}
		catch(Exception exception)
		{
			if(log == null)
				return;
			
			log.print_log("'Runnable auto_save_runnable' exception inside UtilityThreads.java class", exception);
		}
	};
	
	public Runnable combat_data_runnable = () ->
	{
		try
		{
			if(id == null)
				return;
			
			Combat combat = new Combat();
			
			int duration = store.get_by_type(Integer.class, 
					 store.get_data_config("config", "Combat.Duration"), 
					 store.default_combat_duration);
			
			while(duration >= 0)
			{	
				if(is_dead(id))
					return;
				
				if(combat.get_total_entity_damage_taken(id) == 0.0)
					return;
				
				if(combat.get_total_entity_damage_taken(id) != prev_damage_taken)
				{
					duration = store.get_by_type(Integer.class, 
							 store.get_data_config("config", "Combat.Duration"), 
							 store.default_combat_duration);
					
					prev_damage_taken = combat.get_total_entity_damage_taken(id);
				}
				
				duration--;
				
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
			
			combat.remove_attacked_entity(id);
		}
		catch(Exception exception)
		{
			if(log == null)
				return;
			
			log.print_log("'Runnable combat_data_runnable' exception inside UtilityThreads.java class", exception);
		}
	};
	
	@SuppressWarnings("deprecation")
	public Runnable each_player_runnable = () ->
	{
		try
		{
			int errors = 0;
			int max_errors = 300;
			PlayerData pd;
			
			if(player == null)
				return;
			
			each_player_threads.put(player, each_player_thread);
			
			while(true)
			{
				try
				{
					if(errors > max_errors)
						break;
					
					pd = pld.get_player_data(player);
					
					if(pd == null)
						break;
					
					if(player.getHealth() > 0.0)
						pd.set_current_health(player.getHealth());
			
					else
						pd.set_current_health(player.getMaxHealth());
					
					Bukkit.getScheduler().callSyncMethod(Storage.plugin, () ->
					{					
						int mob_count = 0;
						int max_mob_count = 5;
						
						int range = store.get_by_type(Integer.class, 
								 store.get_data_config("config", "Mob.Scanner_Range"), 
								 store.default_mob_scanner_range);
						
						int radius = store.get_by_type(Integer.class, 
								 store.get_data_config("config", "Mob.Lvl_Spread_Radius"), 
								 store.default_mob_lvl_spread_radius);
						
						int scale = store.get_by_type(Integer.class, 
								 store.get_data_config("config", "Mob.Lvl_Scale"), 
								 store.default_mob_lvl_scale);
						
						int max_level = store.get_by_type(Integer.class, 
								 store.get_data_config("config", "Mob.Max_Level"), 
								 store.default_mob_max_level);
						
						Entity entity = utils.get_entity_lookat(player, range);
						
						mob.set_mob_level(entity, player, scale, max_level);
						
						for(Entity e : entity.getNearbyEntities( 
								radius, radius, radius))
						{
							if(mob_count > max_mob_count)
								break;
							
							mob.set_mob_level(e, player, scale, max_level);
							
							mob_count++;
						}
						
						return null;
					});
				}
				catch(Exception ex)
				{
					player.sendMessage(ex.toString());
					errors++;
				}
				
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
			
			each_player_threads.remove(player);
		}
		catch(Exception exception)
		{
			if(log == null)
				return;
			
			log.print_log("'Runnable each_player_runnable' exception inside UtilityThreads.java class", exception);
		}
	};
	
	public UtilityThreads()
	{
		init_data();
	}
	
	public UtilityThreads(UUID id)
	{
		init_data();
		this.id = id;
	}
	
	public UtilityThreads(UUID id, Player player)
	{
		init_data();
		this.id = id;
		this.player = player;
	}
	
	public UtilityThreads(Player player)
	{		
		init_data();
		if(each_player_thread == null)
			each_player_threads = new HashMap<Player, Thread>();
		
		this.player = player;
	}
	
	public void start_thread(Thread t, Runnable r)
	{
		init_data();
		t = new Thread(r);
		t.start();
	}
	
	public boolean is_dead(UUID id)
	{
		Entity e = Bukkit.getEntity(id);
		
		if(e instanceof LivingEntity)
			return ((LivingEntity) e).isDead();
			
		return false;
	}
	
	public void stop_thread(Thread t)
	{
		if(t != null)
			if(t.getState() == Thread.State.RUNNABLE)
				t.interrupt();
	}
	
	public void stop_player_thread(Player p)
	{
		if(each_player_threads.containsKey(p))
			stop_thread(each_player_threads.get(p));
	}
	
	public void stop_players_threads()
	{
		for(Map.Entry<Player, Thread> entry : each_player_threads.entrySet())
		{
			Thread t = entry.getValue();			
			stop_thread(t);
		}
	}
	
	private void init_data()
	{		
		if(store == null)
			store = new Storage();
		
		if(pld == null)
			pld = new PlayerLoadData();
		
		if(log == null)
			log = new PluginLogger();
		
		if(mob == null)
			mob = new MobLevel();
	}
}
