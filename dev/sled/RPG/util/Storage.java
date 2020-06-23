package dev.sled.RPG.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import dev.sled.RPG.Guild;
import dev.sled.RPG.Level;
import dev.sled.RPG.LevelingSystem;
import dev.sled.RPG.Party;
import dev.sled.RPG.PlayerData;
import dev.sled.RPG.PlayerLoadData;
import dev.sled.RPG.Quest;
import net.md_5.bungee.api.ChatColor;

/**

* This class handles all the storage/databse requests

* @version 1.0
* @author Sled
* @since 2020-3-23

*/
public class Storage
{
	public static Plugin plugin = null;
	private File file = null;
	private FileConfiguration file_conf = null;
	private String config;
	public static Double define_MAX_XP_REQ = 1073741824.0;
	
	public double default_player_experience_multiplier = 1.0;
	public double default_player_experience = 0;
	public double default_player_experience_required = 20;
	public int default_player_level = 1;
	public double default_player_experience_required_multiplier = 0.2;
	public double default_player_experience_gain_multiplier = 1.0;
	public double default_experience_gain = 5;
	public int default_combat_duration = 15;
	public double default_money_gain = 1.0;
	public int default_storage_auto_save = 5;
	public boolean default_plugin_leveling_system_status = true;
	public int default_player_milestone_additive_life_boost = 0;
	public int default_player_milestone_additive_damage_boost = 0;
	public double default_player_milestone_multiplicative_experience = 0.1;
	public double default_player_milestone_additive_experience = 0.0;
	public double default_player_milestone_multiplicative_damage_boost = 1.0;
	public int defaul_playert_killstreak_start = 5;
	public double default_player_killstreak_multiplicative_kill_count = 0.5;
	public double default_player_killstreak_additive_experience = 0.1;
	public int default_player_killstreak_max_count = 5;
	public int default_player_max_level = 100;
	public double default_mob_experience_multiplier = 5.0;
	public int default_mob_scanner_range = 10;
	public int default_mob_lvl_spread_radius = 5;
	public int default_mob_lvl_scale = 5;
	public int default_mob_max_level = 100;

	public static double start_player_experience;
	public static double start_player_experience_required;
	public static int start_player_level;
	public static double start_player_experience_required_multiplier;
	public static double start_player_experience_gain_multiplier;
	public static int start_player_milestone_additive_life_boost;
	public static int start_player_milestone_additive_damage_boost;
	public static double start_player_milestone_multiplicative_experience;
	public static double start_player_milestone_additive_experience;
	public static double start_player_milestone_multiplicative_damage_boost;
	public static int start_player_killstreak_start;
	public static double start_player_killstreak_multiplicative_kill_count;
	public static double start_player_killstreak_additive_experience;
	public static int start_player_killstreak_max_count;
	public static int start_player_max_level;
	public static int start_combat_duration;
	public static int start_storage_auto_save;
	public static boolean start_plugin_leveling_system_status;
	public static double start_mob_experience_multiplier;
	public static int start_mob_scanner_range;
	public static int start_mob_lvl_spread_radius;
	public static int start_mob_lvl_scale;
	public static int start_mob_max_level;
	
	public static double database_version = 1.0;
	
	public Storage(Plugin plugin)
	{
		Storage.plugin = plugin;
	}
	
	public Storage()
	{}
	
	/*
	 * Helper function which sets the default values 
	 * of the config file
	 */
	private void set_defaults()
	{
		if(plugin == null)
			return;
		
		String path = "";
		
		//Setting defaults
		
		/// PLAYER
		
		path = "Player.Start_Current_Experience";
		plugin.getConfig().addDefault(path, default_player_experience);
		
		path = "Player.Start_Experience_Required";
		plugin.getConfig().addDefault(path, default_player_experience_required);
		
		path = "Player.Start_Level";
		plugin.getConfig().addDefault(path, default_player_level);
		
		path = "Player.Experience_Required_Multiplier_When_Leveling";
		plugin.getConfig().addDefault(path, default_player_experience_required_multiplier);
		
		path = "Player.Experience_Gain_Multiplier";
		plugin.getConfig().addDefault(path, default_player_experience_gain_multiplier);
		
		path = "Player.Milestone.Additive_Life_Boost";
		plugin.getConfig().addDefault(path, default_player_milestone_additive_life_boost);
		
		path = "Player.Milestone.Additive_Damage_Boost";
		plugin.getConfig().addDefault(path, default_player_milestone_additive_damage_boost);
		
		path = "Player.Milestone.Multiplicative_Experience";
		plugin.getConfig().addDefault(path, default_player_milestone_multiplicative_experience);
		
		path = "Player.Milestone.Additive_Experience";
		plugin.getConfig().addDefault(path, default_player_milestone_additive_experience);
		
		path = "Player.Milestone.Multiplicative_Damage_Boost";
		plugin.getConfig().addDefault(path, default_player_milestone_multiplicative_damage_boost);
		
		path = "Player.KillStreak.Start";
		plugin.getConfig().addDefault(path, defaul_playert_killstreak_start);
		
		path = "Player.KillStreak.Multiplicative_Kill_Count";
		plugin.getConfig().addDefault(path, default_player_killstreak_multiplicative_kill_count);
		
		path = "Player.KillStreak.Additive_Experience";
		plugin.getConfig().addDefault(path, default_player_killstreak_additive_experience);
		
		path = "Player.KillStreak.Max_Count";
		plugin.getConfig().addDefault(path, default_player_killstreak_max_count);
		
		path = "Player.Max_Level";
		plugin.getConfig().addDefault(path, default_player_max_level);
		
		/// END
		
		
		/// MOB
		
		path = "Mob.Experience_Multiplier_Per_Mob_Level";
		plugin.getConfig().addDefault(path, default_mob_experience_multiplier);
		
		path = "Mob.Scanner_Range";
		plugin.getConfig().addDefault(path, default_mob_scanner_range);
		
		path = "Mob.Lvl_Spread_Radius";
		plugin.getConfig().addDefault(path, default_mob_lvl_spread_radius);
		
		path = "Mob.Lvl_Scale";
		plugin.getConfig().addDefault(path, default_mob_lvl_scale);
		
		path = "Mob.Max_Level";
		plugin.getConfig().addDefault(path, default_mob_max_level);
		
		/// END
		
		
		/// COMBAT
		
		path = "Combat.Duration";
		plugin.getConfig().addDefault(path, default_combat_duration);
		
		/// END
		
		
		/// STORAGE
		
		path = "Storage.Auto_Save";
		plugin.getConfig().addDefault(path, default_storage_auto_save);
		
		/// END
		
		
		/// PLUGIN
		
		path = "Plugin.Enable_Leveling_System";
		plugin.getConfig().addDefault(path, default_plugin_leveling_system_status);
		
		/// END
	}
	
	public void load_configuration()
	{
		set_config("config");
		
		start_player_experience = 
				get_by_type(Double.class, get_data("Player.Start_Current_Experience"), default_player_experience);
		start_player_experience_required = 
				get_by_type(Double.class, get_data("Player.Start_Experience_Required"), default_player_experience_required);
		start_player_level =
				get_by_type(Integer.class, get_data("Player.Start_Level"), default_player_level);
		start_player_killstreak_max_count =
				get_by_type(Integer.class, get_data("Player.KillStreak.Max_Count"), default_player_killstreak_max_count);
		start_player_max_level =
				get_by_type(Integer.class, get_data("Player.Max_Level"), default_player_max_level);
		start_combat_duration = 
				get_by_type(Integer.class, get_data("Combat.Duration"), default_combat_duration);
		start_storage_auto_save = 
				get_by_type(Integer.class, get_data("Storage.Auto_Save"), default_storage_auto_save);
		start_player_experience_gain_multiplier =
				get_by_type(Double.class, get_data("Player.Experience_Gain_Multiplier"), default_player_experience_gain_multiplier);
		start_plugin_leveling_system_status =
				get_by_type(Boolean.class, get_data("Plugin.Enable_Leveling_System"), default_plugin_leveling_system_status);
		start_player_experience_required_multiplier = 
				get_by_type(Double.class, get_data("Player.Experience_Required_Multiplier_When_Leveling"), default_player_experience_required_multiplier);
		start_player_milestone_additive_life_boost = 
				get_by_type(Integer.class, get_data("Player.Milestone.Additive_Life_Boost"), default_player_milestone_additive_life_boost);
		start_player_milestone_additive_damage_boost = 
				get_by_type(Integer.class, get_data("Player.Milestone.Additive_Damage_Boost"), default_player_milestone_additive_damage_boost);
		start_player_milestone_multiplicative_experience = 
				get_by_type(Double.class, get_data("Player.Milestone.Multiplicative_Experience"), default_player_milestone_multiplicative_experience);
		start_player_milestone_additive_experience = 
				get_by_type(Double.class, get_data("Player.Milestone.Additive_Experience"), default_player_milestone_additive_experience);
		start_player_milestone_multiplicative_damage_boost = 
				get_by_type(Double.class, get_data("Player.Milestone.Multiplicative_Damage_Boost"), default_player_milestone_multiplicative_damage_boost);
		start_player_killstreak_start =
				get_by_type(Integer.class, get_data("Player.KillStreak.Start"), defaul_playert_killstreak_start);
		start_player_killstreak_multiplicative_kill_count =
				get_by_type(Double.class, get_data("Player.KillStreak.Multiplicative_Kill_Count"), default_player_killstreak_multiplicative_kill_count);
		start_player_killstreak_additive_experience = 
				get_by_type(Double.class, get_data("Player.KillStreak.Additive_Experience"), default_player_killstreak_additive_experience);
		start_mob_experience_multiplier = 
				get_by_type(Double.class, get_data("Mob.Experience_Multiplier_Per_Mob_Level"), default_mob_experience_multiplier);
		start_mob_scanner_range = 
				get_by_type(Integer.class, get_data("Mob.Scanner_Range"), default_mob_scanner_range);
		start_mob_lvl_spread_radius = 
				get_by_type(Integer.class, get_data("Mob.Lvl_Spread_Radius"), default_mob_lvl_spread_radius);
		start_mob_lvl_scale = 
				get_by_type(Integer.class, get_data("Mob.Lvl_Scale"), default_mob_lvl_scale);
		start_mob_max_level = 
				get_by_type(Integer.class, get_data("Mob.Max_Level"), default_mob_max_level);		
	}
	
	/*
	 *  Function which loads the config file
	 */
	public void load_config()
	{		
		if(plugin == null)
			return;
		
		if(plugin != null)
		{
			set_defaults();
			
			// Getting the defaults
		     plugin.getConfig().options().copyDefaults(true);
		     
		     // Save the configs
		     plugin.saveConfig();
		}
	}
	
	/*
	 * Getters.
	 * 
	 * Function which returns the money a mob drops
	 */
	public double get_money_gain_mob(String name)
	{
		return get_by_type(Double.class, get_data_config("MobsDatabase", "Mob." + name + ".money"), 0.0);
	}
	
	/*
	 * Getters.
	 * 
	 * Function which returns the experience a mob drops
	 */
	public double get_experience_gain_mob(String name)
	{
		return get_by_type(Integer.class, get_data_config("MobsDatabase", "Mob." + name + ".experience"), 0);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the data to the config database
	 * 
	 * @param String
	 * @param Object
	 */
	public void set_data_config(String path, Object value)
	{
		Storage store = new Storage();
		
		if (value == null)
			return;
		
		store.set_config("config");
		
		store.set_data(path, value);
	}
	
	public void set_config(String config)
	{
		this.config = config;
		
		file = new File(plugin.getDataFolder(), config + ".yml");
		
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create the" + config + ".yml file!");
			}
		}
		
		file_conf = YamlConfiguration.loadConfiguration(file);
		
		try
		{
			file_conf.save(file);
		}
		catch (IOException e)
		{
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save to the " + config + ".yml file!");
		}
	}
	
	public void set_data(String key, Object data)
	{
		if(file == null)
			return;
		
		file_conf.set(key, data);
		
		try
		{
			file_conf.save(file);
		}
		catch (IOException e)
		{
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save to the " + config + ".yml file!");
		}
	}
	
	public Object get_data(String key)
	{
		return file_conf.get(key);
	}
	
	public Object get_data_config(String path, String key)
	{
		FileConfiguration fc = null;
		File f = null;
		Object o;
		
		if(file_conf != null)
		{
			f = file;
			fc = file_conf;
		}
		
		set_config(path);
		
		o = file_conf.get(key);
		
		if(f != null)
		{
			file = f;
			file_conf = fc;
		}
		
		return o;
	}
	
	public PlayerData load_data(String name, UUID id)
	{
		set_config("LevelStore");
		
		Level level = new Level(
				get_by_type(Double.class, get_data(id + ".current_experience"), default_player_experience), 
				start_player_experience_required,
				get_by_type(Integer.class, get_data(id + ".level"), default_player_level));
		
		level.set_experience_required(Math.round(level.get_experience_required() * Math.pow(start_player_experience_required_multiplier + 1, level.get_level() - 1)));
		
		PlayerData pd = new PlayerData(
			name, new Guild(), new Party(), new Quest(), level, 1.0, Storage.database_version);
		
		set_config("DataStore");
		
		pd.set_damage_dealt(get_by_type(Double.class, get_data(id + ".damage_dealt"), pd.get_damage_dealt()));
		pd.set_mobs_killed(get_by_type(Double.class, get_data(id + ".mobs_killed"), pd.get_mobs_killed()));
		pd.set_players_killed(get_by_type(Double.class, get_data(id + ".players_killed"), pd.get_players_killed()));
		pd.set_total_xp(get_by_type(Double.class, get_data(id + ".total_xp"), pd.get_total_xp()));
		pd.set_experience_multiplier(get_by_type(Double.class, get_data(id + ".experience_multiplier"), pd.get_experience_multiplier()));
		pd.set_experience_gain_multiplier(get_by_type(Double.class, get_data(id + ".experience_gain_multiplier"), pd.get_experience_gain_multiplier()));
		pd.set_database_version(get_by_type(Double.class, get_data(id + ".database_version"), pd.get_database_version()));
		pd.get_player_level().set_damage(get_by_type(Double.class, get_data(id + ".damage"), level.get_damage()));
		pd.get_player_level().set_life(get_by_type(Integer.class, get_data(id + ".life"), level.get_life()));
		pd.set_current_health(get_by_type(Double.class, get_data(id + ".current_life"), pd.get_current_health()));
		pd.set_highest_mob_kill_streak(get_by_type(Double.class, get_data(id + ".mob_kill_streak"), pd.get_highest_mob_kill_streak()));
		pd.set_highest_player_kill_streak(get_by_type(Double.class, get_data(id + ".player_kill_streak"), pd.get_highest_player_kill_streak()));
		
		return pd;
	}
	
	public void save_data(UUID id, PlayerData pd)
	{
		// Saving the player mapping to the other object types first
		set_config("DataStore");
		
		set_data(id + ".player_name", pd.get_player_name());
		set_data(id + ".player_guild", pd.get_player_guild().get_guild_name());
		set_data(id + ".player_quest", pd.get_player_quest().get_quest_id());
		set_data(id + ".damage_dealt", pd.get_damage_dealt());
		set_data(id + ".mobs_killed", pd.get_mobs_killed());
		set_data(id + ".players_killed", pd.get_players_killed());
		set_data(id + ".total_xp", pd.get_total_xp());
		set_data(id + ".experience_multiplier", pd.get_experience_multiplier());
		set_data(id + ".experience_gain_multiplier", pd.get_experience_gain_multiplier());
		set_data(id + ".database_version", pd.get_database_version());
		set_data(id + ".damage", pd.get_player_level().get_damage());
		set_data(id + ".life", pd.get_player_level().get_life());
		set_data(id + ".current_life", pd.get_current_health());
		set_data(id + ".mob_kill_streak", pd.get_highest_mob_kill_streak());
		set_data(id + ".player_kill_streak", pd.get_highest_player_kill_streak());
		
		// Saving the player object types
		
		/// LEVEL
		
		set_config("LevelStore");
		
		set_data(id + ".current_experience", pd.get_player_level().get_current_experience());
		set_data(id + ".level", pd.get_player_level().get_level());
		
		/// END
	}
	
	@SuppressWarnings("deprecation")
	public void add_effects(Player player)
	{
		PlayerLoadData pld = new PlayerLoadData();
		PlayerData pd = pld.get_player_data(player);
		Level lvl = pd.get_player_level();
		
		if(!player.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
		{
			int value = lvl.get_life();
			
			if(!get_by_type(Boolean.class, get_data_config("config", "Plugin.Enable_Leveling_System"), start_plugin_leveling_system_status))
				return;
			
			if(value > 0)
			{
				player.addPotionEffect(PotionEffectType.HEALTH_BOOST.createEffect(99999, value - 1));
				
				if(pd.get_current_health() > player.getMaxHealth())
					player.setHealth(player.getMaxHealth());
				
				else
					player.setHealth(pd.get_current_health());
			}
			else
				pd.set_current_health(player.getHealth());
		}
	}
	
	public void add_effects()
	{
		PlayerLoadData pld = new PlayerLoadData();
		Map<Player, PlayerData> map = pld.get_all_player_data();
		
		for(Map.Entry<Player, PlayerData> entry : map.entrySet())
		{
			Player player = entry.getKey();			
			add_effects(player);
		}
	}
	
	public void remove_effects(Player player)
	{
		if(player.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
			player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
	}
	
	public void remove_effects()
	{
		PlayerLoadData pld = new PlayerLoadData();
		Map<Player, PlayerData> map = pld.get_all_player_data();
		
		for(Map.Entry<Player, PlayerData> entry : map.entrySet())
		{
			Player player = entry.getKey();			
			remove_effects(player);
		}
	}
	
	public void save_all_data()
	{
		PlayerLoadData pld = new PlayerLoadData();
		Map<Player, PlayerData> map = pld.get_all_player_data();
		
		for(Map.Entry<Player, PlayerData> entry : map.entrySet())
		{
			Player player = entry.getKey();
			PlayerData pd = pld.get_player_data(player);
			
			save_data(player.getUniqueId(), pd);
		}
	}
	
	public <T> T get_by_type(Class<T> type, Object check, Object _default)
	{
		if(check != null)
			return type.cast(check);

		return type.cast(_default);
	}
	
	/**
	 * Reload configs
	 */
	@EventHandler
	public void on_player_message(PlayerCommandPreprocessEvent event)
	{
		PlayerLoadData pld = new PlayerLoadData();
		
		if(plugin == null)
			return;
		
		Player sender = event.getPlayer();
		String [] command = event.getMessage().split(" ");
		
		if(command[0].equals("/gp_reload_leveling_system"))
		{		
			if(sender.hasPermission("gp.reload.permission"))
			{
				sender.sendMessage(ChatColor.DARK_AQUA + "RELOADING LEVELING SYSTEM!");
				plugin.reloadConfig();
				load_configuration();
				
				Map<Player, PlayerData> map = pld.get_all_player_data();
				LevelingSystem ls = new LevelingSystem();
				
				for(Map.Entry<Player, PlayerData> entry : map.entrySet())
				{
					Player player = entry.getKey();
					PlayerData pd = pld.get_player_data(player);
					ls.milestone(player, pd, true);
					
					pd.get_player_level().set_experience_required(Math.round(start_player_experience_required * 
							Math.pow(start_player_experience_required_multiplier + 1, pd.get_player_level().get_level() - 1)));
				}
			}
		}
	}
}
